package com.qrbats.qrbats.functionalities.event.controller;

import com.qrbats.qrbats.entity.event.Event;
import com.qrbats.qrbats.functionalities.event.dto.EventDto;
import com.qrbats.qrbats.functionalities.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/v1/event")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EventController {

    private final EventService eventService;

    // Endpoint to get an event by its ID
    @GetMapping("/get-all-event-for-user")
    public ResponseEntity<List<Event>> getAllEventForUser() {
        try {
            List<Event> eventList = eventService.getAllEventForUser();
            return new ResponseEntity<>(eventList, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // Endpoint to save an event (create or update)
    @PostMapping("/save-event")
    public ResponseEntity<Event> saveEvent(@RequestBody EventDto eventDto) {
        try {
            Event savedEvent = eventService.saveEvent(eventDto);
            return new ResponseEntity<>(savedEvent, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
