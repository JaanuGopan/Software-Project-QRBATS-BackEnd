package com.qrbats.qrbats.functionalities.event.service;

import com.qrbats.qrbats.config.AuditInfoProvider;
import com.qrbats.qrbats.entity.event.Event;
import com.qrbats.qrbats.entity.event.EventRepository;
import com.qrbats.qrbats.entity.lecture.Lecture;
import com.qrbats.qrbats.entity.lecture.LectureRepository;
import com.qrbats.qrbats.entity.lecture.LectureStatus;
import com.qrbats.qrbats.entity.module.Module;
import com.qrbats.qrbats.entity.timetable.TimeTable;
import com.qrbats.qrbats.functionalities.event.dto.EventDto;
import com.qrbats.qrbats.functionalities.module.service.ModuleService;
import com.qrbats.qrbats.service.scheduler.DynamicSchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final AuditInfoProvider auditInfoProvider;

    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));
    }

    // Save an event, either creating or updating it
    public Event saveEvent(EventDto eventDto) {
        // Convert the EventDto to an Event entity
        Event event = new Event();
        if (eventDto.getId() != null) {
            // Fetch existing event if updating
            event = eventRepository.findById(eventDto.getId())
                    .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventDto.getId()));
        }

        // Map fields from EventDto to Event
        event.setEventName(eventDto.getEventName());
        event.setEventDate(eventDto.getEventDate());
        event.setLocationId(eventDto.getLocationId());
        event.setEventStartTime(eventDto.getEventStartTime());
        event.setEventEndTime(eventDto.getEventEndTime());
        event.setCreatedBy(auditInfoProvider.getCurrentUserIdentifier());

        // Save the event and return the saved entity
        return eventRepository.save(event);
    }

    public List<Event> getAllEventForUser() {
        return eventRepository.findAllByCreatedBy(auditInfoProvider.getCurrentUserIdentifier())
                .orElseThrow(() -> new IllegalArgumentException("Event not found."));
    }
}
