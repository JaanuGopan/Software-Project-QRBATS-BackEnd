package com.qrbats.qrbats.functionalities.attendance.lecture.service;

import com.qrbats.qrbats.authentication.entities.user.User;
import com.qrbats.qrbats.authentication.services.UserService;
import com.qrbats.qrbats.config.AuditInfoProvider;
import com.qrbats.qrbats.entity.attendance.lecture.LectureAttendance;
import com.qrbats.qrbats.entity.attendance.lecture.LectureAttendanceRepository;
import com.qrbats.qrbats.entity.attendance.AttendanceStatus;
import com.qrbats.qrbats.entity.lecture.Lecture;
import com.qrbats.qrbats.entity.location.Location;
import com.qrbats.qrbats.entity.module.Module;
import com.qrbats.qrbats.entity.module_enrollment.ModuleEnrollment;
import com.qrbats.qrbats.functionalities.attendance.lecture.dto.LectureAttendanceResultDto;
import com.qrbats.qrbats.functionalities.lecture.service.LectureService;
import com.qrbats.qrbats.functionalities.location.service.LocationService;
import com.qrbats.qrbats.functionalities.module.service.ModuleService;
import com.qrbats.qrbats.functionalities.module.service.ModuleEnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.qrbats.qrbats.functionalities.attendance.lecture.dto.LectureAttendanceMarkingDto;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LectureAttendanceService {
    private final LectureAttendanceRepository lectureAttendanceRepository;
    private final LectureService lectureService;
    private final UserService userService;
    private final ModuleService moduleService;
    private final ModuleEnrollmentService moduleEnrollmentService;
    private final LocationService locationService;
    private final AuditInfoProvider auditInfoProvider;

    @Value("${attendance.lateAttendanceMinutes}")
    private Integer lateAttendanceMinutes;

    public LectureAttendance markAttendance(LectureAttendanceMarkingDto lectureAttendanceMarkingDto) {

        // Retrieve necessary entities
        Lecture lecture = lectureService.findLectureById(lectureAttendanceMarkingDto.getLectureId());
        User student = userService.getAuthenticatedUser();
        Module module = moduleService.getModuleByModuleId(lecture.getModuleId());
        Location location = locationService.getLocationById(lecture.getLocationId());

        // Check module enrollment
        ModuleEnrollment moduleEnrollment = moduleEnrollmentService
                .getModuleEnrollmentByStudentIdAndModuleId(student.getUserId(), module.getId());

        // Validate lecture date and time
        if (!lecture.getLectureDate().equals(Date.valueOf(LocalDate.now()))) {
            throw new IllegalArgumentException("Attendance date does not match the lecture date.");
        }

        if (Time.valueOf(LocalTime.now()).before(lecture.getStartTime())) {
            throw new IllegalArgumentException("Attendance time cannot be before the lecture start time.");
        }

        if (Time.valueOf(LocalTime.now()).after(lecture.getEndTime())) {
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
        long fifteenMinutesAfterStart = lecture.getStartTime().getTime() + (lateAttendanceMinutes * 60 * 1000);
        if (Time.valueOf(LocalTime.now()).getTime() > fifteenMinutesAfterStart) {
            attendanceStatus = AttendanceStatus.LATE;
        } else {
            attendanceStatus = AttendanceStatus.PRESENT;
        }

        LectureAttendance lectureAttendance = new LectureAttendance();
        lectureAttendance.setAttendedDate(Date.valueOf(LocalDate.now()));
        lectureAttendance.setAttendanceStatus(attendanceStatus);
        lectureAttendance.setAttendedTime(Time.valueOf(LocalTime.now()));
        lectureAttendance.setAttendeeId(student.getUserId());
        lectureAttendance.setLectureId(lectureAttendanceMarkingDto.getLectureId());

        return lectureAttendanceRepository.save(lectureAttendance);
    }

    public List<LectureAttendance> getAllAttendanceByLectureId(Long lectureId) {
        return lectureAttendanceRepository.findAllByLectureId(lectureId)
                .orElseThrow(() -> new  IllegalArgumentException("There is no attendance found for this lecture."));
    }

    public List<LectureAttendance> getAllAttendanceByModuleId(Integer moduleId) {
        return lectureAttendanceRepository.findAllByModuleId(moduleId)
                .orElseThrow(() -> new  IllegalArgumentException("There is no attendance found for this module."));
    }

    public List<LectureAttendance> getAllAttendanceByModuleIdAndStudentId(Integer moduleId) {
        return lectureAttendanceRepository
                .findAllByModuleIdAndAttendeeId(moduleId, auditInfoProvider.getCurrentUserIdentifier())
                .orElseThrow(() -> new  IllegalArgumentException("There is no attendance found for this module."));
    }

    public LectureAttendance getAttendanceByLectureIdAndStudentId(Long lectureId) {
        return lectureAttendanceRepository
                .findByLectureIdAndAttendeeId(lectureId, auditInfoProvider.getCurrentUserIdentifier())
                .orElseThrow(() -> new  IllegalArgumentException("There is no attendance found for this module."));
    }

    public Boolean checkStudentAttendanceForLecture(Integer studentId, Long lectureId) {
        return lectureAttendanceRepository
                .findByLectureIdAndAttendeeId(lectureId, auditInfoProvider.getCurrentUserIdentifier()).isPresent();
    }

    public List<LectureAttendanceResultDto> getAllAttendanceForLecture(Long lectureId) {
        setNotAttendedStudentAttendanceForLecture(lectureId);

        User user = userService.getAuthenticatedUser();
        List<LectureAttendance> lectureAttendanceList = getAllAttendanceByLectureId(lectureId);
        List<LectureAttendanceResultDto> lectureAttendanceResultDtoList = new ArrayList<>();
        lectureAttendanceList.forEach((attendance -> {
            LectureAttendanceResultDto lectureAttendanceResultDto = new LectureAttendanceResultDto();
            lectureAttendanceResultDto.setAttendedTime(attendance.getAttendedTime());
            lectureAttendanceResultDto.setAttendedDate(attendance.getAttendedDate());
            lectureAttendanceResultDto.setAttendanceStatus(attendance.getAttendanceStatus());
            lectureAttendanceResultDto.setStudentName(user.getFirstName());
            lectureAttendanceResultDto.setIndexNumber(user.getIndexNumber());

            lectureAttendanceResultDtoList.add(lectureAttendanceResultDto);
        }));

        return lectureAttendanceResultDtoList;
    }

    public List<LectureAttendanceResultDto> getAllAttendanceForModule(Integer moduleId) {
        setNotAttendedStudentAttendanceForModule(moduleId);

        User user = userService.getAuthenticatedUser();
        List<LectureAttendance> lectureAttendanceList = getAllAttendanceByModuleId(moduleId);
        List<LectureAttendanceResultDto> lectureAttendanceResultDtoList = new ArrayList<>();
        lectureAttendanceList.forEach((attendance -> {
            LectureAttendanceResultDto lectureAttendanceResultDto = new LectureAttendanceResultDto();
            lectureAttendanceResultDto.setAttendedTime(attendance.getAttendedTime());
            lectureAttendanceResultDto.setAttendedDate(attendance.getAttendedDate());
            lectureAttendanceResultDto.setAttendanceStatus(attendance.getAttendanceStatus());
            lectureAttendanceResultDto.setStudentName(user.getFirstName());
            lectureAttendanceResultDto.setIndexNumber(user.getIndexNumber());

            lectureAttendanceResultDtoList.add(lectureAttendanceResultDto);
        }));

        return lectureAttendanceResultDtoList;
    }

    public List<LectureAttendanceResultDto> getAllModuleAttendanceForStudent(Integer moduleId) {
        setNotAttendedStudentAttendanceForModule(moduleId);

        User user = userService.getAuthenticatedUser();
        List<LectureAttendance> lectureAttendanceList = getAllAttendanceByModuleIdAndStudentId(moduleId);
        List<LectureAttendanceResultDto> lectureAttendanceResultDtoList = new ArrayList<>();
        lectureAttendanceList.forEach((attendance -> {
            LectureAttendanceResultDto lectureAttendanceResultDto = new LectureAttendanceResultDto();
            lectureAttendanceResultDto.setAttendedTime(attendance.getAttendedTime());
            lectureAttendanceResultDto.setAttendedDate(attendance.getAttendedDate());
            lectureAttendanceResultDto.setAttendanceStatus(attendance.getAttendanceStatus());
            lectureAttendanceResultDto.setStudentName(user.getFirstName());
            lectureAttendanceResultDto.setIndexNumber(user.getIndexNumber());

            lectureAttendanceResultDtoList.add(lectureAttendanceResultDto);
        }));

        return lectureAttendanceResultDtoList;
    }

    public LectureAttendanceResultDto getStudentAttendanceForLecture(Long lectureId) {
        setNotAttendedStudentAttendanceForLecture(lectureId);

        User user = userService.getAuthenticatedUser();
        LectureAttendance lectureAttendance = getAttendanceByLectureIdAndStudentId(lectureId);
        LectureAttendanceResultDto lectureAttendanceResultDto = new LectureAttendanceResultDto();
        lectureAttendanceResultDto.setAttendedTime(lectureAttendance.getAttendedTime());
        lectureAttendanceResultDto.setAttendedDate(lectureAttendance.getAttendedDate());
        lectureAttendanceResultDto.setAttendanceStatus(lectureAttendance.getAttendanceStatus());
        lectureAttendanceResultDto.setStudentName(user.getFirstName());
        lectureAttendanceResultDto.setIndexNumber(user.getIndexNumber());

        return lectureAttendanceResultDto;
    }

    public void setNotAttendedStudentAttendanceForLecture(Long lectureId) {
        Lecture lecture = lectureService.findLectureById(lectureId);
        List<ModuleEnrollment> moduleEnrollmentList = moduleEnrollmentService
                .getModuleEnrollmentByModuleId(lecture.getModuleId());
        if (lecture.getLectureDate().equals(Date.valueOf(LocalDate.now()))
                || lecture.getLectureDate().after(Date.valueOf(LocalDate.now()))) {
            if (lecture.getEndTime().after(Time.valueOf(LocalTime.now()))) {
                List<LectureAttendance> lectureAttendanceList = getAllAttendanceByLectureId(lectureId);

                for (ModuleEnrollment moduleEnrollment : moduleEnrollmentList) {
                    boolean isStudentAttended = false;
                    for (LectureAttendance lectureAttendance : lectureAttendanceList) {
                        if (moduleEnrollment.getStudentId().equals(lectureAttendance.getAttendeeId())){
                            isStudentAttended = true;
                        }
                    }
                    if (!isStudentAttended) {
                        LectureAttendance lectureAttendance = new LectureAttendance();
                        lectureAttendance.setAttendanceStatus(AttendanceStatus.ABSENT);
                        lectureAttendance.setLectureId(lectureId);
                        lectureAttendance.setAttendedDate(null);
                        lectureAttendance.setAttendedTime(null);
                        lectureAttendance.setAttendeeId(moduleEnrollment.getStudentId());

                        lectureAttendanceRepository.save(lectureAttendance);
                    }
                }
            }
        }
        lectureAttendanceRepository.flush(); // Ensure database updates are committed immediately
    }

    public void setNotAttendedStudentAttendanceForModule(Integer moduleId) {
        List<Lecture> lectureList = lectureService.getALlLectureByModuleId(moduleId);
        lectureList.forEach(lecture -> {
            setNotAttendedStudentAttendanceForLecture(lecture.getId());
        });
    }


}
