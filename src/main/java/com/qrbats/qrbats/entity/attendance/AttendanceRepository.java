/*
package com.qrbats.qrbats.entity.attendance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceEvent, Integer> {
    Optional<AttendanceEvent> findByEventIdAndAttendeeId(
            Integer eventId,
            Integer attendeeId
    );

    Optional<List<AttendanceEvent>> findAllByEventId(Integer eventId);
    Optional<List<AttendanceEvent>> findAllByAttendeeId(Integer attendeeId);
    void deleteAllByEventId(Integer eventId);

}*/
