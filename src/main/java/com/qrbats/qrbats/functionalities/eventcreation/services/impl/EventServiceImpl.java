package com.qrbats.qrbats.functionalities.eventcreation.services.impl;

import com.qrbats.qrbats.entity.attendance.AttendanceEvent;
import com.qrbats.qrbats.entity.event.Event;
import com.qrbats.qrbats.entity.event.EventRepository;
import com.qrbats.qrbats.entity.event.EventRole;
import com.qrbats.qrbats.entity.module.ModuleRepository;
import com.qrbats.qrbats.functionalities.attendance.service.impl.AttendanceEventService;
import com.qrbats.qrbats.functionalities.eventcreation.dto.RegisterEventRequest;
import com.qrbats.qrbats.functionalities.eventcreation.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    @Autowired
    private final EventRepository eventRepository;
    @Autowired
    private final ModuleRepository moduleRepository;
    @Autowired
    private final AttendanceEventService attendanceEventService;

    @Override
    public Event createEvent(RegisterEventRequest request) {
        Optional<Event> existEvent;
        Event event;
        if (request.getEventId() != null) {
            existEvent = eventRepository.findById(request.getEventId());
        } else {
            existEvent = eventRepository
                    .findByEventNameAndEventDateAndEventTimeAndEventModuleCode(
                            request.getEventName(), request.getEventDate(), request.getEventTime(), request.getEventModuleName()
                    );
        }


        event = existEvent.orElseGet(Event::new);
        event.setEventName(request.getEventName());
        event.setEventDate(request.getEventDate());
        event.setEventValidDate(request.getEventValidDate());
        event.setEventTime(request.getEventTime());
        event.setEventEndTime(request.getEventEndTime());
        event.setEventVenue(request.getEventVenue());
        event.setEventRole(EventRole.valueOf(request.getEventRole()));
        event.setEventAssignedUserId(request.getEventAssignedUserId());
        if (request.getEventModuleName() != null) {
            event.setEventModuleCode(request.getEventModuleName());
        } else {
            event.setEventModuleCode(null);
        }


        Event save = eventRepository.save(event);
        if (!eventRepository.findById(save.getEventId()).isPresent()) throw new RuntimeException("Event Saved Failed.");
        attendanceEventService.createEventAttendanceTable(save.getEventId());
        return save;
    }

    public Optional<Event> findAlreadyExistEvent(RegisterEventRequest request) {
        return eventRepository.findByEventNameAndEventDateAndEventTimeAndEventModuleCode(
                request.getEventName(), request.getEventDate(), request.getEventTime(), request.getEventModuleName()
        );
    }

    @Override
    public List<Event> getAllEventByModuleCode(String moduleCode) {
        Optional<List<Event>> eventList = eventRepository.findAllByEventModuleCode(moduleCode);
        if (eventList.isPresent()) {
            return eventList.get();
        } else {
            throw new RuntimeException("Given module code has no events.");
        }
    }

    @Override
    public List<Event> getAllEvent() {
        return eventRepository.findAll();
    }

    @Override
    public List<Event> getAllEventByUserId(Integer userId) {
        Optional<List<Event>> eventList = eventRepository.findAllByEventAssignedUserId(userId);
        if (eventList.isPresent()) {
            return eventList.get();
        } else {
            throw new RuntimeException("Fail to get event for given userId.");
        }
    }

    @Override
    public void deleteEvent(Integer id) {
        Optional<Event> event = eventRepository.findById(id);
        if (!event.isPresent()) throw new RuntimeException("The event not found for given eventId");
        try{
            attendanceEventService.dropEventAttendanceTable(id);
            eventRepository.deleteById(id);
        }catch (Exception ex){
            throw new RuntimeException("Error deleteEvent. ",ex);
        }

    }

    @Override
    public Event getEventByEventId(Integer eventId) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (!event.isPresent()) throw new RuntimeException("No Event Found For This Event Id.");
        return event.get();
    }

}
