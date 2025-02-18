package com.qrbats.qrbats.entity.lecture;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.Optional;
@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {
    Optional<List<Lecture>> findAllByModuleId(Integer moduleId);

    @Query("SELECT l FROM Lecture l " +
            "WHERE l.lectureDate = :lectureDate " +
            "AND l.locationId = :locationId " +
            "AND ((:startTime BETWEEN l.startTime AND l.endTime) " +
            "OR (:endTime BETWEEN l.startTime AND l.endTime) " +
            "OR (l.startTime BETWEEN :startTime AND :endTime))")
    List<Lecture> findConflictingLectures(
            @Param("lectureDate") Date lectureDate,
            @Param("locationId") Integer locationId,
            @Param("startTime") Time startTime,
            @Param("endTime") Time endTime
    );

    Optional<List<Lecture>> findAllByLectureStatus(LectureStatus lectureStatus);
    Optional<List<Lecture>> findAllByLectureStatusAndModuleId(LectureStatus lectureStatus, Integer moduleId);
}
