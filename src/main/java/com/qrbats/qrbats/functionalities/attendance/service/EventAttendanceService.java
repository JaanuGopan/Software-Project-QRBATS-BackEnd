package com.qrbats.qrbats.functionalities.attendance.service;

import com.qrbats.qrbats.authentication.entities.user.User;
import com.qrbats.qrbats.authentication.services.UserService;
import com.qrbats.qrbats.config.AuditInfoProvider;
import com.qrbats.qrbats.entity.attendance.AttendanceStatus;
import com.qrbats.qrbats.entity.attendance.event.EventAttendance;
import com.qrbats.qrbats.entity.attendance.event.EventAttendanceRepository;
import com.qrbats.qrbats.entity.lecture.Lecture;
import com.qrbats.qrbats.entity.location.Location;
import com.qrbats.qrbats.functionalities.attendance.dto.EventAttendanceMarkingDto;
import com.qrbats.qrbats.functionalities.attendance.dto.EventAttendanceViewDto;
import com.qrbats.qrbats.functionalities.attendance.dto.LectureAttendanceResultDto;
import com.qrbats.qrbats.functionalities.lecture.service.LectureService;
import com.qrbats.qrbats.functionalities.location.service.LocationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventAttendanceService {
    private final EventAttendanceRepository attendanceRepository;
    private final LectureService lectureService;
    private final UserService userService;
    private final LocationService locationService;
    private final AuditInfoProvider auditInfoProvider;
    private final EventAttendanceRepository eventAttendanceRepository;

    @Value("${attendance.lateAttendanceMinutes}")
    private Integer lateAttendanceMinutes;

    @Transactional
    public EventAttendance markEventAttendance(EventAttendanceMarkingDto eventAttendanceMarkingDto) {
        // Retrieve necessary entities
        Lecture lecture = lectureService.findLectureById(eventAttendanceMarkingDto.getEventId());
        Location location = locationService.getLocationById(lecture.getLocationId());

        if (attendanceRepository.findByEventIdAndAttendeeId(eventAttendanceMarkingDto.getEventId(), auditInfoProvider.getCurrentUserIdentifier()).isPresent()) {
            throw new IllegalArgumentException("You already mark attendance for this event.");
        }

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
        if (!locationService.checkInTheLocation(eventAttendanceMarkingDto.getLocationCoordinates(), location)) {
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

        EventAttendance eventAttendance = new EventAttendance();
        eventAttendance.setEventId(eventAttendanceMarkingDto.getEventId());
        eventAttendance.setAttendanceStatus(attendanceStatus);
        eventAttendance.setAttendedTime(LocalTime.now());
        eventAttendance.setAttendedDate(LocalDate.now());
        eventAttendance.setAttendeeId(auditInfoProvider.getCurrentUserIdentifier());

        return attendanceRepository.save(eventAttendance);
    }

    public List<LectureAttendanceResultDto> getAllAttendanceForEvent(Long eventId) {
        Lecture event = lectureService.findLectureById(eventId);
        List<LectureAttendanceResultDto> attendanceResultDtoList = new ArrayList<>();

        List<EventAttendance> eventAttendanceList = attendanceRepository.findAllByEventId(eventId)
                .orElse(new ArrayList<>());

        for (EventAttendance eventAttendance : eventAttendanceList) {
            User student = userService.getUserById(eventAttendance.getAttendeeId());
            LectureAttendanceResultDto lectureAttendanceResultDto = new LectureAttendanceResultDto();
            lectureAttendanceResultDto.setStudentName(student.getFirstName());
            lectureAttendanceResultDto.setIndexNumber(student.getIndexNumber());

            lectureAttendanceResultDto.setAttendedTime(eventAttendance.getAttendedTime());
            lectureAttendanceResultDto.setAttendedDate(eventAttendance.getAttendedDate());
            lectureAttendanceResultDto.setAttendanceStatus(eventAttendance.getAttendanceStatus());
            attendanceResultDtoList.add(lectureAttendanceResultDto);
        }

        return attendanceResultDtoList;
    }

    public List<EventAttendanceViewDto> getAllAttendanceForStudent() {
        List<EventAttendanceViewDto> attendanceViewDtoList = new ArrayList<>();

        List<EventAttendance> eventAttendanceList = attendanceRepository
                .findAllByAttendeeId(auditInfoProvider.getCurrentUserIdentifier()).orElse(new ArrayList<>());

        for (EventAttendance eventAttendance : eventAttendanceList) {
            EventAttendanceViewDto eventAttendanceViewDto = new EventAttendanceViewDto(
                    eventAttendance.getId(),
                    eventAttendance.getAttendeeId(),
                    eventAttendance.getEventId(),
                    lectureService.getLectureById(eventAttendance.getEventId()).getName(),
                    eventAttendance.getAttendedDate(),
                    eventAttendance.getAttendedTime(),
                    eventAttendance.getAttendanceStatus()
            );
            attendanceViewDtoList.add(eventAttendanceViewDto);
        }
        return attendanceViewDtoList;
    }

    @Transactional
    public void deleteEventAttendanceByAttendeeId(Integer attendeeId) {
        eventAttendanceRepository.deleteAllByAttendeeId(attendeeId);
    }
}
