package com.qrbats.qrbats.functionalities.eventcreation.services;

import com.qrbats.qrbats.entity.event.Event;
import com.qrbats.qrbats.entity.event.EventRepository;
import com.qrbats.qrbats.entity.event.EventRole;
import com.qrbats.qrbats.functionalities.eventcreation.dto.RegisterEventRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class EventServiceImplTest {

    @Autowired
    private EventService underTestEventService;
    @Autowired
    private EventRepository eventRepository;

    @AfterEach
    void tearDown() {
        eventRepository.deleteAll();
    }

    private Event addSampleEvent(){
        Event event1 = new Event();
        event1.setEventRole(EventRole.LECTURE);
        event1.setEventName("Test Lecture 1");
        event1.setEventDate(LocalDate.now());
        event1.setEventTime(LocalTime.parse("08:30:00"));
        event1.setEventEndTime(LocalTime.parse("08:30:00").plusHours(2));
        event1.setEventVenue("NCC");
        event1.setEventAssignedUserId(2);
        return event1;
    }

    @Test
    void testCreateEventWithValidDetails() {

        // given
        Event sampleEvent = addSampleEvent();
        RegisterEventRequest request = new RegisterEventRequest();
        request.setEventDate(sampleEvent.getEventDate());
        request.setEventName(sampleEvent.getEventName());
        request.setEventRole(String.valueOf(sampleEvent.getEventRole()));
        request.setEventTime(sampleEvent.getEventTime());
        request.setEventAssignedUserId(sampleEvent.getEventAssignedUserId());
        request.setEventEndTime(sampleEvent.getEventEndTime());

        // when
        Event expectedEvent = underTestEventService.createEvent(request);


        // then
        assertThat(expectedEvent).isNotNull();
        assertThat(expectedEvent).isEqualTo(eventRepository.findById(expectedEvent.getEventId()).get());

    }

    @Test
    void testCreateEventWithInvalidDetails(){
        // given
        Event sampleEvent = addSampleEvent();
        RegisterEventRequest request = new RegisterEventRequest();
        request.setEventDate(sampleEvent.getEventDate());
        request.setEventName(sampleEvent.getEventName());
        request.setEventRole(String.valueOf(sampleEvent.getEventRole()));
        request.setEventTime(sampleEvent.getEventTime());
        request.setEventAssignedUserId(sampleEvent.getEventAssignedUserId());
        request.setEventEndTime(sampleEvent.getEventEndTime());

        // when
        Event expectedEvent = underTestEventService.createEvent(request);


        // then
        assertThat(expectedEvent).isNotNull();
        assertThat(expectedEvent).isEqualTo(eventRepository.findById(expectedEvent.getEventId()).get());
    }

    @Test
    void findAlreadyExistEvent() {
    }

    @Test
    void getAllEventByModuleCode() {
    }

    @Test
    void getAllEvent() {
    }

    @Test
    void getAllEventByUserId() {
    }

    @Test
    void deleteEvent() {
    }
}