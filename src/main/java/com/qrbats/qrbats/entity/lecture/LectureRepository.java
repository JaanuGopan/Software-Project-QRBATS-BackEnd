package com.qrbats.qrbats.entity.lecture;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
    Optional<List<Lecture>> findAllByCreatedByAndLectureType(Integer createdBy, LectureType lectureType);
    Optional<List<Lecture>> findAllByCreatedByAndModuleId(Integer createdBy, Integer moduleId);

    @Query("SELECT l FROM Lecture l WHERE l.lectureDate = :lectureDate " +
            "AND l.locationId = :locationId " +
            "AND (:lectureTime BETWEEN l.startTime AND l.endTime " +
            "OR :lectureTimeEnd BETWEEN l.startTime AND l.endTime " +
            "OR l.startTime BETWEEN :lectureTime AND :lectureTimeEnd)")
    Optional<Lecture> findLectureConflict(
            @Param("lectureDate") LocalDate lectureDate,
            @Param("locationId") Integer locationId,
            @Param("lectureTime") LocalTime lectureTime,
            @Param("lectureTimeEnd") LocalTime lectureTimeEnd);

    void deleteAllByModuleId(Integer moduleId);
}
