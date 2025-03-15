package com.qrbats.qrbats.entity.lecture;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {
    Optional<List<Lecture>> findAllByModuleId(Integer moduleId);
    Optional<List<Lecture>> findAllByLectureStatus(LectureStatus lectureStatus);
    Optional<List<Lecture>> findAllByLectureStatusAndModuleId(LectureStatus lectureStatus, Integer moduleId);
    Optional<List<Lecture>> findAllByCreatedBy(Integer createdBy);
    Optional<List<Lecture>> findAllByCreatedByAndModuleId(Integer createdBy, Integer moduleId);
    Optional<Lecture> findByLectureDateAndLocationIdAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
            LocalDate lectureDate, Integer locationId, LocalTime lectureTime, LocalTime lectureTimeEnd);
}
