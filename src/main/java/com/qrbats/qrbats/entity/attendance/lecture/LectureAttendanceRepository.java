package com.qrbats.qrbats.entity.attendance.lecture;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LectureAttendanceRepository extends JpaRepository<LectureAttendance, Long> {

    Optional<List<LectureAttendance>> findAllByLectureId(Long lectureId);
    Optional<List<LectureAttendance>> findAllByModuleId(Integer moduleId);
    Optional<List<LectureAttendance>> findAllByModuleIdAndAttendeeId(Integer moduleId, Integer attendeeId);
    Optional<LectureAttendance> findByLectureIdAndAttendeeId(Long lectureId, Integer attendeeId);
}
