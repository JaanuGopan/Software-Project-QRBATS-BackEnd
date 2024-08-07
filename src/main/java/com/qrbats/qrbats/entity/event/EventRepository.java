package com.qrbats.qrbats.entity.event;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event,Integer> {
    Optional<Event> findByEventNameAndEventDateAndEventTime(
            String eventName, LocalDate eventDate, LocalTime eventTime
    );
    Optional<List<Event>> findAllByEventVenueAndEventDate(String venue,LocalDate date);
    Optional<List<Event>> findAllByEventAssignedUserId(Integer userId);

    Optional<List<Event>> findAllByEventVenue(String venue);


}
