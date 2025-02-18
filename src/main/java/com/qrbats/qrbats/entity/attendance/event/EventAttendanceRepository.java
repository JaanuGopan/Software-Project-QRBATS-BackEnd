package com.qrbats.qrbats.entity.attendance.event;

import com.qrbats.qrbats.entity.attendance.lecture.LectureAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventAttendanceRepository extends JpaRepository<EventAttendance, Long> {
    Optional<List<EventAttendance>> findAllByEventId(Integer eventId);
    Optional<List<EventAttendance>> findAllByAttendeeId(Integer attendeeId);
    Optional<EventAttendance> findByEventIdAndAttendeeId(Integer eventId, Integer attendeeId);
}
