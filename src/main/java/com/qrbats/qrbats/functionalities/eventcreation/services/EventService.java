package com.qrbats.qrbats.functionalities.eventcreation.services;

import com.qrbats.qrbats.entity.event.Event;
import com.qrbats.qrbats.functionalities.eventcreation.dto.RegisterEventRequest;

import java.util.List;

public interface EventService {
    Event createEvent(RegisterEventRequest request);
    List<Event> getAllEventByModuleCode(String moduleCode);
    List<Event> getAllEvent();

    void deleteEvent(Integer id);

}
