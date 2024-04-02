package com.qrbats.qrbats.entity.attendance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    Optional<Attendance> findByEventIdAndAttendeeId(
            Integer eventId,
            Integer attendeeId
    );

    Optional<List<Attendance>> findAllByEventId(Integer eventId);
    Optional<List<Attendance>> findAllByAttendeeId(Integer attendeeId);
    void deleteAllByEventId(Integer eventId);

}