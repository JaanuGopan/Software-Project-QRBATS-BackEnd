package com.qrbats.qrbats.functionalities.attendance.service;

import com.qrbats.qrbats.authentication.entities.user.User;
import com.qrbats.qrbats.authentication.services.UserService;
import com.qrbats.qrbats.config.AuditInfoProvider;
import com.qrbats.qrbats.entity.attendance.*;
import com.qrbats.qrbats.entity.lecture.Lecture;
import com.qrbats.qrbats.entity.location.Location;
import com.qrbats.qrbats.entity.module.Module;
import com.qrbats.qrbats.entity.module_enrollment.ModuleEnrollment;
import com.qrbats.qrbats.functionalities.attendance.dto.LectureAttendanceMarkingDto;
import com.qrbats.qrbats.functionalities.attendance.dto.LectureAttendanceResultDto;
import com.qrbats.qrbats.functionalities.attendance.dto.ModuleAttendanceResultDto;
import com.qrbats.qrbats.functionalities.attendance.dto.student.StudentAttendanceLectureDto;
import com.qrbats.qrbats.functionalities.attendance.dto.student.StudentAttendanceResultDto;
import com.qrbats.qrbats.functionalities.department.service.DepartmentService;
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
    private final DepartmentService departmentService;

    @Value("${attendance.lateAttendanceMinutes}")
    private Integer lateAttendanceMinutes;

    public Attendance markAttendance(LectureAttendanceMarkingDto lectureAttendanceMarkingDto) {

        // Retrieve necessary entities
        Lecture lecture = lectureService.findLectureById(lectureAttendanceMarkingDto.getLectureId());
        User student = userService.getAuthenticatedUser();
        Location location = locationService.getLocationById(lecture.getLocationId());

        // Validate lecture date and time
        if (!lecture.getLectureDate().equals(LocalDate.now())) {
            throw new IllegalArgumentException("Attendance date does not match the lecture date.");
        }

        if (lecture.getStartTime().isAfter(LocalTime.now())) {
            throw new IllegalArgumentException("Attendance time cannot be before the lecture start time.");
        }

        if (lecture.getEndTime().isBefore(LocalTime.now())) {
            throw new IllegalArgumentException("Attendance time cannot be after the lecture end time.");
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

        Module module = moduleService.getModuleByModuleId(lecture.getModuleId());
        // Check module enrollment
        ModuleEnrollment moduleEnrollment = moduleEnrollmentService
                .getModuleEnrollmentByStudentIdAndModuleId(student.getUserId(), module.getId());

        if (!student.getSemester().equals(module.getSemester())) {
            throw new IllegalArgumentException("Student is not in the correct semester for this module.");
        }
        if (!student.getBatch().equals(module.getStudentBatch())) {
            throw new IllegalArgumentException("Student batch does not match the module's batch.");
        }

        Optional<Attendance> attendance = attendanceRepository
                .findByAttendeeIdAndModuleIdAndLectureId(student.getUserId(), module.getId(), lecture.getId());
        if (attendance.isPresent()) {
            throw  new IllegalArgumentException("You already mark your attendance for this lecture.");
        }

       Attendance saveAttendance = new Attendance();
        saveAttendance.setAttendeeId(student.getUserId());
        saveAttendance.setLectureId(lecture.getId());
        saveAttendance.setModuleId(module.getId());
        saveAttendance.setAttendedDate(LocalDate.now());
        saveAttendance.setAttendedTime(LocalTime.now());
        saveAttendance.setAttendanceStatus(attendanceStatus);

        // Save and return updated attendance record
        return attendanceRepository.save(saveAttendance);
    }

    public Boolean checkStudentAttendanceForLecture(Integer studentId, Long lectureId) {
        Optional<Attendance> optionalAttendance = attendanceRepository.findByAttendeeIdAndLectureId(studentId, lectureId);
        return optionalAttendance.isPresent();
    }


    public List<LectureAttendanceResultDto> getAllAttendanceForLecture(Long lectureId) {
        Lecture lecture = lectureService.findLectureById(lectureId);
        List<ModuleEnrollment> moduleEnrollmentList = moduleEnrollmentService
                .getModuleEnrollmentByModuleId(lecture.getModuleId());
        List<LectureAttendanceResultDto> attendanceResultDtoList = new ArrayList<>();

        for (ModuleEnrollment moduleEnrollment : moduleEnrollmentList) {
            User user = userService.getUserById(moduleEnrollment.getStudentId());
            Optional<Attendance> optionalAttendance = attendanceRepository
                    .findByAttendeeIdAndLectureId(user.getUserId(), lectureId);

            LectureAttendanceResultDto lectureAttendanceResultDto = new LectureAttendanceResultDto();
            lectureAttendanceResultDto.setStudentName(user.getFirstName());
            lectureAttendanceResultDto.setIndexNumber(user.getIndexNumber());

            if (optionalAttendance.isPresent()) {
                Attendance attendance = optionalAttendance.get();
                lectureAttendanceResultDto.setAttendedTime(attendance.getAttendedTime());
                lectureAttendanceResultDto.setAttendedDate(attendance.getAttendedDate());
                lectureAttendanceResultDto.setAttendanceStatus(attendance.getAttendanceStatus());
            } else {
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
            if (lectureService.checkLectureIsFinished(lecture) || (lecture.getLectureDate().isEqual(LocalDate.now())
                    && lecture.getStartTime().isBefore(LocalTime.now()))) {
                finishedLectureHours = finishedLectureHours.plus(lectureService.calculateLectureDuration(lecture));
                finishedLectureCount++;
            }
        }

        // Loop through all students enrolled in the module
        for (ModuleEnrollment moduleEnrollment : moduleEnrollmentList) {
            User student = userService.getUserById(moduleEnrollment.getStudentId());
            int attendedLectureCount = 0;
            Duration attendedLectureHours = Duration.ZERO;
            List<Long> attendedLectureIdList = new ArrayList<>();

            ModuleAttendanceResultDto moduleAttendanceResultDto = new ModuleAttendanceResultDto();
            moduleAttendanceResultDto.setStudentName(student.getFirstName());
            moduleAttendanceResultDto.setIndexNumber(student.getIndexNumber());

            List<Attendance> attendedAttendanceList = attendanceRepository
                    .findAllByAttendeeIdAndModuleId(student.getUserId(), moduleId).orElse(new ArrayList<>());

            for (Lecture lecture : lectureList) {
                for (Attendance attendance : attendedAttendanceList) {
                    if (attendance.getLectureId().equals(lecture.getId())) {
                        attendedLectureCount++;
                        attendedLectureHours = attendedLectureHours.plus(lectureService.calculateLectureDuration(lecture));
                        attendedLectureIdList.add(lecture.getId());
                    }
                }
            }
            double attendancePercentage = finishedLectureHours.toMinutes() == 0
                    ? 0.0
                    : ((double) attendedLectureHours.toMinutes() / finishedLectureHours.toMinutes()) * 100;

            moduleAttendanceResultDto.setAttendedLectureIdList(attendedLectureIdList);
            moduleAttendanceResultDto.setAttendanceHours(attendedLectureHours);
            moduleAttendanceResultDto.setAttendanceCount(attendedLectureCount);
            moduleAttendanceResultDto.setFinishedLectureCount(finishedLectureCount);
            moduleAttendanceResultDto.setFinishedLectureHours(finishedLectureHours);
            moduleAttendanceResultDto.setAttendancePercentage(attendancePercentage);
            moduleAttendanceResultDto.setNotAttendanceHours(finishedLectureHours.minus(attendedLectureHours));
            moduleAttendanceResultDto.setNotAttendanceCount(finishedLectureCount - attendedLectureCount);

            moduleAttendanceResultDtoList.add(moduleAttendanceResultDto);
        }

        return moduleAttendanceResultDtoList;
    }


    public StudentAttendanceResultDto getModuleAttendanceForStudent(Integer moduleId) {
        User student = userService.getAuthenticatedUser();
        Module module = moduleService.getModuleByModuleId(moduleId);
        List<Lecture> lectureList = lectureService.getALlLectureByModuleId(moduleId);

        StudentAttendanceResultDto studentAttendanceResultDto = new StudentAttendanceResultDto();
        List<StudentAttendanceLectureDto> studentAttendancesLectureList = new ArrayList<>();
        Duration attendanceHours = Duration.ZERO;
        Duration notAttendanceHours = Duration.ZERO;
        Duration finishedLectureHours = Duration.ZERO;
        int attendanceCount = 0;
        int finishedLectureCount = 0;

        List<Attendance> lectureAttendanceList = attendanceRepository
                .findAllByAttendeeIdAndModuleId(student.getUserId(), moduleId).orElse(new ArrayList<>());


        // Loop through lectures to gather attendance data
        for (Lecture lecture : lectureList) {
            // Calculate the duration for finished lectures
            if (lectureService.checkLectureIsFinished(lecture)
                    || (lecture.getLectureDate().isEqual(LocalDate.now())
                    && lecture.getStartTime().isBefore(LocalTime.now()))) {

                finishedLectureCount++;
                finishedLectureHours = finishedLectureHours.plus(lectureService.calculateLectureDuration(lecture));

                StudentAttendanceLectureDto studentAttendanceLectureDto = new StudentAttendanceLectureDto();
                studentAttendanceLectureDto.setLectureName(lecture.getName());
                studentAttendanceLectureDto.setLectureDate(lecture.getLectureDate());
                studentAttendanceLectureDto.setLectureStartTime(lecture.getStartTime());
                studentAttendanceLectureDto.setLectureEndTime(lecture.getEndTime());
                studentAttendanceLectureDto.setLocationName(locationService.getLocationName(lecture.getLocationId()));

                boolean isAttended = false;

                for (Attendance attendance : lectureAttendanceList) {
                    if (attendance.getLectureId().equals(lecture.getId())) {
                        attendanceHours = attendanceHours.plus(lectureService.calculateLectureDuration(lecture));
                        attendanceCount++;

                        isAttended = true;
                        studentAttendanceLectureDto.setAttendanceStatus(attendance.getAttendanceStatus());
                        studentAttendanceLectureDto.setAttendedDate(attendance.getAttendedDate());
                        studentAttendanceLectureDto.setAttendedTime(attendance.getAttendedTime());
                    }
                }

                if (!isAttended) {
                    studentAttendanceLectureDto.setAttendedDate(null);
                    studentAttendanceLectureDto.setAttendedTime(null);
                    studentAttendanceLectureDto.setAttendanceStatus(AttendanceStatus.ABSENT);
                }

                studentAttendancesLectureList.add(studentAttendanceLectureDto);
            }
        }

        // Calculate attendance percentage
        double attendancePercentage = (finishedLectureHours.toMinutes() == 0) ? 0.0 :
                ((double) attendanceHours.toMinutes() / finishedLectureHours.toMinutes()) * 100;

        // Populate the result DTO
        studentAttendanceResultDto.setStudentAttendancesLectureList(studentAttendancesLectureList);
        studentAttendanceResultDto.setAttendanceCount(attendanceCount);
        studentAttendanceResultDto.setNotAttendanceCount(finishedLectureCount - attendanceCount);
        studentAttendanceResultDto.setFinishedLectureCount(finishedLectureCount);
        studentAttendanceResultDto.setAttendanceHours(attendanceHours.toHours() + " hours " + attendanceHours.toMinutes() % 60 + " minutes");
        studentAttendanceResultDto.setNotAttendanceHours(notAttendanceHours.toHours() + " hours " + notAttendanceHours.toMinutes() % 60 + " minutes");
        studentAttendanceResultDto.setFinishedLectureHours(finishedLectureHours.toHours() + " hours " + finishedLectureHours.toMinutes() % 60 + " minutes");
        studentAttendanceResultDto.setAttendancePercentage(attendancePercentage);

        return studentAttendanceResultDto;
    }

    public void deleteAttendanceForStudent(Integer studentId) {
        attendanceRepository.deleteAllByAttendeeId(studentId);
    }
}
