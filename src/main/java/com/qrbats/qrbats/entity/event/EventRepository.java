package com.qrbats.qrbats.entity.event;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event,Integer> {
    Optional<List<Event>> findAllByEventModuleCode(String moduleCode);
    Optional<Event> findByEventNameAndEventDateAndEventTimeAndEventModuleCode(
            String eventName, LocalDate eventDate, LocalTime eventTime, String eventModuleCode
    );
    Optional<List<Event>> findAllByEventAssignedUserId(Integer userId);


}
