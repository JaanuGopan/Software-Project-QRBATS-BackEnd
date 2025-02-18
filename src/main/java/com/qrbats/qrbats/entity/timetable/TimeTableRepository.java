package com.qrbats.qrbats.entity.timetable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface TimeTableRepository extends JpaRepository<TimeTable,Integer> {

    @Query("SELECT t FROM TimeTable t " +
            "WHERE t.dayOfWeek = :dayOfWeek " +
            "AND t.locationId = :locationId " +
            "AND ((:startTime BETWEEN t.startTime AND t.endTime) " +
            "OR (:endTime BETWEEN t.startTime AND t.endTime) " +
            "OR (t.startTime BETWEEN :startTime AND :endTime))")
    List<TimeTable> findConflictingTimeTables(
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("locationId") Integer locationId,
            @Param("startTime") Time startTime,
            @Param("endTime") Time endTime
    );

}
