package com.qrbats.qrbats.entity.attendance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<List<Attendance>> findAllByAttendeeId(Integer attendeeId);

    Optional<List<Attendance>> findAllByModuleId(Integer moduleId);

    Optional<List<Attendance>> findAllByLectureId(Long lectureId);

    Optional<List<Attendance>> findAllByAttendeeIdAndModuleId(Integer attendeeId, Integer moduleId);

    Optional<Attendance> findByAttendeeIdAndLectureId(Integer attendeeId, Long lectureId);

    Optional<Attendance> findByAttendeeIdAndModuleIdAndLectureId(Integer attendeeId, Integer moduleId, Long lectureId);

    void deleteAllByAttendeeId(Integer attendeeId);
}
