package com.qrbats.qrbats.functionalities.eventcreation.controller;

import com.qrbats.qrbats.entity.event.Event;
import com.qrbats.qrbats.functionalities.eventcreation.dto.DeleteByIdRequest;
import com.qrbats.qrbats.functionalities.eventcreation.dto.GetAllEventByModuleCodeRequest;
import com.qrbats.qrbats.functionalities.eventcreation.dto.RegisterEventRequest;
import com.qrbats.qrbats.functionalities.eventcreation.services.impl.EventServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/v1/event")
@CrossOrigin("http://localhost:3000")
@RequiredArgsConstructor
public class EventCreationController {

    private final EventServiceImpl eventService;


    @PostMapping("/create")
    public ResponseEntity<Event> createEvent(@RequestBody RegisterEventRequest request) {
        return ResponseEntity.ok(eventService.createEvent(request));
    }

    @PostMapping("/getalleventbymodulecode")
    public ResponseEntity<List<Event>> getAllEventByModuleCode(
            @RequestBody GetAllEventByModuleCodeRequest getAllEventByModuleCodeRequest
    ){
        return ResponseEntity.ok(eventService.getAllEventByModuleCode(
                getAllEventByModuleCodeRequest.getModuleCode()
        ));
    }

    @PostMapping("/getallevents")
    public ResponseEntity<List<Event>> findAllEvents(){
       return ResponseEntity.ok(eventService.getAllEvent());
    }

    @PostMapping("deletebyid")
    public void deleteById(@RequestBody DeleteByIdRequest request){
        eventService.deleteEvent(request.getEventId());
    }


}
