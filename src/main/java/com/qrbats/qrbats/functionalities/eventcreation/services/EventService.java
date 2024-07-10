package com.qrbats.qrbats.functionalities.eventcreation.services;

import com.qrbats.qrbats.entity.event.Event;
import com.qrbats.qrbats.functionalities.eventcreation.dto.RegisterEventRequest;

import java.util.List;

public interface EventService {
    Event createEvent(RegisterEventRequest request);
    Event updateEvent(RegisterEventRequest request);
    List<Event> getAllEvent();

    List<Event> getAllEventByUserId(Integer userId);

    boolean deleteEvent(Integer id);
    Event getEventByEventId(Integer eventId);

}
