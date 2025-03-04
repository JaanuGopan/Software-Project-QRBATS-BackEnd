package com.qrbats.qrbats.functionalities.attendance.service;

import com.qrbats.qrbats.authentication.entities.user.User;
import com.qrbats.qrbats.authentication.services.UserService;
import com.qrbats.qrbats.config.AuditInfoProvider;
import com.qrbats.qrbats.entity.attendance.Attendance;
import com.qrbats.qrbats.entity.attendance.AttendanceRepository;
import com.qrbats.qrbats.entity.attendance.AttendanceStatus;
import com.qrbats.qrbats.entity.attendance.LectureAttendance;
import com.qrbats.qrbats.entity.lecture.Lecture;
import com.qrbats.qrbats.entity.location.Location;
import com.qrbats.qrbats.entity.module.Module;
import com.qrbats.qrbats.entity.module_enrollment.ModuleEnrollment;
import com.qrbats.qrbats.functionalities.attendance.dto.LectureAttendanceMarkingDto;
import com.qrbats.qrbats.functionalities.attendance.dto.LectureAttendanceResultDto;
import com.qrbats.qrbats.functionalities.attendance.dto.ModuleAttendanceResultDto;
import com.qrbats.qrbats.functionalities.attendance.dto.student.StudentAttendanceLectureDto;
import com.qrbats.qrbats.functionalities.attendance.dto.student.StudentAttendanceResultDto;
import com.qrbats.qrbats.functionalities.lecture.service.LectureService;
import com.qrbats.qrbats.functionalities.location.service.LocationService;
import com.qrbats.qrbats.functionalities.module.service.ModuleService;
import com.qrbats.qrbats.functionalities.module.service.ModuleEnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LectureAttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final LectureService lectureService;
    private final UserService userService;
    private final ModuleService moduleService;
    private final ModuleEnrollmentService moduleEnrollmentService;
    private final LocationService locationService;
    private final AuditInfoProvider auditInfoProvider;

    @Value("${attendance.lateAttendanceMinutes}")
    private Integer lateAttendanceMinutes;

    public Attendance markAttendance(LectureAttendanceMarkingDto lectureAttendanceMarkingDto) {

        // Retrieve necessary entities
        Lecture lecture = lectureService.findLectureById(lectureAttendanceMarkingDto.getLectureId());
        User student = userService.getAuthenticatedUser();
        Module module = moduleService.getModuleByModuleId(lecture.getModuleId());
        Location location = locationService.getLocationById(lecture.getLocationId());

        // Check module enrollment
        ModuleEnrollment moduleEnrollment = moduleEnrollmentService
                .getModuleEnrollmentByStudentIdAndModuleId(student.getUserId(), module.getId());

        // Validate lecture date and time
        if (!lecture.getLectureDate().equals(LocalDate.now())) {
            throw new IllegalArgumentException("Attendance date does not match the lecture date.");
        }

        if (lecture.getStartTime().isBefore(LocalTime.now())) {
            throw new IllegalArgumentException("Attendance time cannot be before the lecture start time.");
        }

        if (lecture.getEndTime().isBefore(LocalTime.now())) {
            throw new IllegalArgumentException("Attendance time cannot be after the lecture end time.");
        }

        // Validate student eligibility
        if (!student.getDepartmentId().equals(module.getDepartmentId())) {
            throw new IllegalArgumentException("Student does not belong to the same department as the module.");
        }

        if (!student.getSemester().equals(module.getSemester())) {
            throw new IllegalArgumentException("Student is not in the correct semester for this module.");
        }

        if (!student.getBatch().equals(module.getStudentBatch())) {
            throw new IllegalArgumentException("Student batch does not match the module's batch.");
        }

        // Location check
        if (!locationService.checkInTheLocation(lectureAttendanceMarkingDto.getLocationCoordinates(), location)) {
            throw new IllegalArgumentException("Student is not within the lecture location.");
        }

        // Determine attendance status (Late or Present)
        AttendanceStatus attendanceStatus;
        LocalTime fifteenMinutesAfterStart = lecture.getStartTime().plusMinutes(lateAttendanceMinutes);
        if (fifteenMinutesAfterStart.isBefore(LocalTime.now())) {
            attendanceStatus = AttendanceStatus.LATE;
        } else {
            attendanceStatus = AttendanceStatus.PRESENT;
        }

        // Retrieve or create Attendance entry
        Attendance attendance = attendanceRepository.findByAttendeeId(student.getUserId())
                .orElseGet(() -> {
                    Attendance newAttendance = new Attendance();
                    newAttendance.setAttendeeId(student.getUserId());
                    newAttendance.setAttendanceData(new HashMap<>());
                    return newAttendance;
                });

        // Initialize module entry if missing
        attendance.getAttendanceData()
                .computeIfAbsent(module.getId(), k -> new HashMap<>());

        // Create new LectureAttendance record
        LectureAttendance lectureAttendance = new LectureAttendance();
        lectureAttendance.setAttendedDate(LocalDate.now());
        lectureAttendance.setAttendedTime(LocalTime.now());
        lectureAttendance.setAttendanceStatus(attendanceStatus);

        // Store LectureAttendance inside module's map
        attendance.getAttendanceData().get(module.getId())
                .put(lecture.getId(), lectureAttendance);

        // Save and return updated attendance record
        return attendanceRepository.save(attendance);
    }


    public Attendance getAllAttendanceByAttendeeId(Integer attendeeId) {
        return attendanceRepository.findByAttendeeId(attendeeId)
                .orElseThrow(() -> new  IllegalArgumentException("Attendance not found."));
    }

    public Map<Long, LectureAttendance> getAttendanceDataByModuleIdForStudent(Integer studentId, Integer moduleId) {
        User student = userService.getUserById(studentId);
        Module module = moduleService.getModuleByModuleId(moduleId);
        ModuleEnrollment moduleEnrollment = moduleEnrollmentService
                .getModuleEnrollmentByStudentIdAndModuleId(studentId, moduleId);

        return attendanceRepository.findByAttendeeId(studentId)
                .map(attendance -> attendance.getAttendanceData().getOrDefault(moduleId, new HashMap<>()))
                .orElse(new HashMap<>()); // Return empty map if no attendance found
    }

    public LectureAttendance getLectureAttendanceByLectureIdForStudent(Integer studentId, Long lectureId) {
        Lecture lecture = lectureService.findLectureById(lectureId);
        Module module = moduleService.getModuleByModuleId(lecture.getModuleId());
        ModuleEnrollment moduleEnrollment = moduleEnrollmentService
                .getModuleEnrollmentByStudentIdAndModuleId(studentId, module.getId());

        return attendanceRepository.findByAttendeeId(studentId)
                .map(attendance -> attendance.getAttendanceData()
                        .getOrDefault(module.getId(), Collections.emptyMap())
                        .get(lectureId))
                .orElse(null); // Return null if no attendance found
    }


    public Boolean checkStudentAttendanceForLecture(Integer studentId, Long lectureId) {
        Optional<Attendance> optionalAttendance = attendanceRepository.findByAttendeeId(studentId);
        if (optionalAttendance.isEmpty()) {
            return false;
        }
        Attendance attendance = optionalAttendance.get();
        for (Map<Long, LectureAttendance> lectures : attendance.getAttendanceData().values()) {
            if (lectures.containsKey(lectureId)) {
                return true;
            }
        }
        return false;
    }


    public List<LectureAttendanceResultDto> getAllAttendanceForLecture(Long lectureId) {
        Lecture lecture = lectureService.findLectureById(lectureId);
        List<ModuleEnrollment> moduleEnrollmentList = moduleEnrollmentService
                .getModuleEnrollmentByModuleId(lecture.getModuleId());
        List<LectureAttendanceResultDto> attendanceResultDtoList = new ArrayList<>();

        for (ModuleEnrollment moduleEnrollment : moduleEnrollmentList) {
            User user = userService.getUserById(moduleEnrollment.getStudentId());
            Optional<Attendance> optionalAttendance = attendanceRepository.findByAttendeeId(user.getUserId());

            LectureAttendanceResultDto lectureAttendanceResultDto = new LectureAttendanceResultDto();
            lectureAttendanceResultDto.setStudentName(user.getFirstName() + " " + user.getLastName());
            lectureAttendanceResultDto.setIndexNumber(user.getIndexNumber());

            if (optionalAttendance.isPresent()) {
                Attendance attendance = optionalAttendance.get();

                // Check if attendanceData contains the lectureId
                Map<Long, LectureAttendance> lectureAttendanceMap = attendance.getAttendanceData()
                        .get(lecture.getModuleId());

                if (lectureAttendanceMap != null && lectureAttendanceMap.containsKey(lectureId)) {
                    LectureAttendance lectureAttendance = lectureAttendanceMap.get(lectureId);
                    lectureAttendanceResultDto.setAttendedTime(lectureAttendance.getAttendedTime());
                    lectureAttendanceResultDto.setAttendedDate(lectureAttendance.getAttendedDate());
                    lectureAttendanceResultDto.setAttendanceStatus(lectureAttendance.getAttendanceStatus());
                } else {
                    // If the lectureId is not found, mark as absent
                    lectureAttendanceResultDto.setAttendedTime(null);
                    lectureAttendanceResultDto.setAttendedDate(null);
                    lectureAttendanceResultDto.setAttendanceStatus(AttendanceStatus.ABSENT);
                }
            } else {
                // If no attendance record exists, mark as absent
                lectureAttendanceResultDto.setAttendedTime(null);
                lectureAttendanceResultDto.setAttendedDate(null);
                lectureAttendanceResultDto.setAttendanceStatus(AttendanceStatus.ABSENT);
            }

            attendanceResultDtoList.add(lectureAttendanceResultDto);
        }

        return attendanceResultDtoList;
    }


    public List<ModuleAttendanceResultDto> getAllAttendanceForModule(Integer moduleId) {
        Module module = moduleService.getModuleByModuleId(moduleId);
        List<ModuleEnrollment> moduleEnrollmentList = moduleEnrollmentService
                .getModuleEnrollmentByModuleId(module.getId());
        List<ModuleAttendanceResultDto> moduleAttendanceResultDtoList = new ArrayList<>();
        List<Lecture> lectureList = lectureService.getALlLectureByModuleId(moduleId);
        Duration finishedLectureHours = Duration.ZERO;
        int finishedLectureCount = 0;
        int totalLectureCount = lectureList.size();

        // Calculate total hours of finished lectures
        for (Lecture lecture : lectureList) {
            if (lectureService.checkLectureIsFinished(lecture)) {
                finishedLectureHours = finishedLectureHours.plus(lectureService.calculateLectureDuration(lecture));
                finishedLectureCount++;
            }
        }

        // Loop through all students enrolled in the module
        for (ModuleEnrollment moduleEnrollment : moduleEnrollmentList) {
            User student = userService.getUserById(moduleEnrollment.getStudentId());
            Optional<Attendance> attendance = attendanceRepository.findByAttendeeId(student.getUserId());

            ModuleAttendanceResultDto moduleAttendanceResultDto = new ModuleAttendanceResultDto();
            moduleAttendanceResultDto.setStudentName(student.getFirstName() + " " + student.getLastName());
            moduleAttendanceResultDto.setIndexNumber(student.getIndexNumber());

            if (attendance.isPresent()) {
                Attendance studentAttendance = attendance.get();

                int attendedLectureCount = 0;
                Duration attendanceLectureHours = Duration.ZERO;

                // Check attendance for each lecture
                for (Lecture lecture : lectureList) {
                    if (studentAttendance.getAttendanceData() != null) {
                        Map<Long, LectureAttendance> lectureAttendanceMap = studentAttendance
                                .getAttendanceData().get(lecture.getModuleId());
                        if (lectureAttendanceMap != null && lectureAttendanceMap.containsKey(lecture.getId())) {
                            LectureAttendance lectureAttendance = lectureAttendanceMap.get(lecture.getId());
                            if (lectureAttendance != null && lectureAttendance.getAttendanceStatus() == AttendanceStatus.PRESENT) {
                                attendedLectureCount++;
                                attendanceLectureHours = attendanceLectureHours.plus(lectureService.calculateLectureDuration(lecture));
                            }
                        }
                    }
                }

                // Set the results in the DTO
                moduleAttendanceResultDto.setAttendanceCount(attendedLectureCount);
                moduleAttendanceResultDto.setFinishedLectureCount(finishedLectureCount);
                moduleAttendanceResultDto.setFinishedLectureHours(finishedLectureHours);

                // Calculate attendance percentage
                double attendancePercentage = finishedLectureHours.toHours() == 0
                        ? 0.0
                        : ((double) attendanceLectureHours.toMinutes() / finishedLectureHours.toMinutes()) * 100;

                moduleAttendanceResultDto.setAttendancePercentage(attendancePercentage);
                moduleAttendanceResultDto.setNotAttendanceCount(finishedLectureCount - attendedLectureCount);
                moduleAttendanceResultDto.setNotAttendanceHours(finishedLectureHours.minus(attendanceLectureHours));

            } else {
                // If no attendance record, mark as absent for all finished lectures
                moduleAttendanceResultDto.setNotAttendanceHours(finishedLectureHours);
                moduleAttendanceResultDto.setFinishedLectureCount(finishedLectureCount);
                moduleAttendanceResultDto.setAttendanceCount(0);
                moduleAttendanceResultDto.setAttendanceHours(Duration.ZERO);
                moduleAttendanceResultDto.setAttendancePercentage(0.0);
                moduleAttendanceResultDto.setNotAttendanceCount(finishedLectureCount);
                moduleAttendanceResultDto.setFinishedLectureHours(finishedLectureHours);
            }

            // Add the attendance results to the list
            moduleAttendanceResultDtoList.add(moduleAttendanceResultDto);
        }

        return moduleAttendanceResultDtoList;
    }


    public StudentAttendanceResultDto getModuleAttendanceForStudent(Integer moduleId) {
        // Get the authenticated student
        User student = userService.getAuthenticatedUser();

        // Fetch the module and lectures related to it
        Module module = moduleService.getModuleByModuleId(moduleId);
        List<Lecture> lectureList = lectureService.getALlLectureByModuleId(moduleId);

        // Initialize DTO and variables
        StudentAttendanceResultDto studentAttendanceResultDto = new StudentAttendanceResultDto();
        List<StudentAttendanceLectureDto> studentAttendancesLectureList = new ArrayList<>();
        Duration attendanceHours = Duration.ZERO;
        Duration notAttendanceHours = Duration.ZERO;
        int attendanceCount = 0;
        int notAttendanceCount = 0;
        Duration finishedLectureHours = Duration.ZERO;
        int finishedLectureCount = 0;

        // Loop through lectures to gather attendance data
        for (Lecture lecture : lectureList) {
            LectureAttendance lectureAttendance = getLectureAttendanceByLectureIdForStudent(student.getUserId(), lecture.getId());

            // Calculate the duration for finished lectures
            if (lectureService.checkLectureIsFinished(lecture)) {
                finishedLectureHours = finishedLectureHours.plus(lectureService.calculateLectureDuration(lecture));
                finishedLectureCount++;
            }

            // Check attendance status
            StudentAttendanceLectureDto studentAttendanceLectureDto = new StudentAttendanceLectureDto();
            studentAttendanceLectureDto.setLectureName(lecture.getName());
            studentAttendanceLectureDto.setLectureDate(lecture.getLectureDate());

            if (lectureAttendance != null) {
                if (lectureAttendance.getAttendanceStatus() == AttendanceStatus.PRESENT) {
                    attendanceCount++;
                    attendanceHours = attendanceHours.plus(lectureService.calculateLectureDuration(lecture));
                    studentAttendanceLectureDto.setAttendanceStatus(AttendanceStatus.PRESENT);
                } else {
                    notAttendanceCount++;
                    notAttendanceHours = notAttendanceHours.plus(lectureService.calculateLectureDuration(lecture));
                    studentAttendanceLectureDto.setAttendanceStatus(AttendanceStatus.ABSENT);
                }
            } else {
                // If no attendance record, mark as ABSENT
                notAttendanceCount++;
                notAttendanceHours = notAttendanceHours.plus(lectureService.calculateLectureDuration(lecture));
                studentAttendanceLectureDto.setAttendanceStatus(AttendanceStatus.ABSENT);
            }

            studentAttendancesLectureList.add(studentAttendanceLectureDto);
        }

        // Calculate attendance percentage
        double attendancePercentage = finishedLectureHours.toHours() == 0
                ? 0.0
                : ((double) attendanceHours.toMinutes() / finishedLectureHours.toMinutes()) * 100;

        // Populate the result DTO
        studentAttendanceResultDto.setStudentAttendancesLectureList(studentAttendancesLectureList);
        studentAttendanceResultDto.setAttendanceCount(attendanceCount);
        studentAttendanceResultDto.setNotAttendanceCount(notAttendanceCount);
        studentAttendanceResultDto.setFinishedLectureCount(finishedLectureCount);
        studentAttendanceResultDto.setAttendanceHours(attendanceHours);
        studentAttendanceResultDto.setNotAttendanceHours(notAttendanceHours);
        studentAttendanceResultDto.setFinishedLectureHours(finishedLectureHours);
        studentAttendanceResultDto.setAttendancePercentage(attendancePercentage);

        return studentAttendanceResultDto;
    }


}
