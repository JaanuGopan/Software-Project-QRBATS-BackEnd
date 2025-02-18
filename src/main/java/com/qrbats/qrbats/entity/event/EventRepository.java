package com.qrbats.qrbats.entity.event;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event,Integer> {
    Optional<List<Event>> findAllByLocationIdAndEventDate(Integer locationId, LocalDate date);
    Optional<List<Event>> findAllByCreatedBy(Integer userId);

    Optional<List<Event>> findAllByLocationId(Integer locationId);

    Optional<List<Event>> findAllByEventDate(LocalDate date);

    @Query("SELECT l FROM Event l " +
            "WHERE l.eventDate = :eventDate " +
            "AND l.locationId = :locationId " +
            "AND ((:eventStartTime BETWEEN l.eventStartTime AND l.eventEndTime) " +
            "OR (:eventEndTime BETWEEN l.eventStartTime AND l.eventEndTime) " +
            "OR (l.eventStartTime BETWEEN :eventStartTime AND :eventEndTime))")
    List<Event> findConflictingEvent(
            @Param("eventDate") LocalDate eventDate,
            @Param("locationId") Integer locationId,
            @Param("eventStartTime") LocalTime eventStartTime,
            @Param("eventEndTime") LocalTime eventEndTime
    );

}
