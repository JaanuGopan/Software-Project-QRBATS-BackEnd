package com.qrbats.qrbats.functionalities.eventcreation;

import com.qrbats.qrbats.entity.event.Event;
import com.qrbats.qrbats.entity.event.EventRepository;
import com.qrbats.qrbats.entity.event.EventRole;
import com.qrbats.qrbats.entity.module.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventCreationService {

    private final EventRepository repository;
    private final ModuleRepository moduleRepository;


    public void createEvent(RegisterEventRequest request){
        Event event = new Event();
        event.setEventName(request.getEventName());
        event.setEventDate(request.getEventDate());
        event.setEventValidDate(request.getEventValidDate());
        event.setEventTime(request.getEventTime());
        event.setEventVenue(request.getEventVenue());
        event.setEventRole(EventRole.valueOf(request.getEventRole()));
        event.setEventAssignedUserId(request.getEventAssignedUserId());
        if(request.getEventModuleName() != null){
            event.setEventModuleId(moduleRepository.findByModuleCode(request.getEventModuleName()).get(0).getModuleId());
        }else {
            event.setEventModuleId(null);
        }

        repository.save(event);
    }

}
