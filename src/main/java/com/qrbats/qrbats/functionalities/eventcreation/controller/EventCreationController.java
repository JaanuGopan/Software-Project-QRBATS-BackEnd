package com.qrbats.qrbats.functionalities.eventcreation.controller;

import com.qrbats.qrbats.entity.event.Event;
import com.qrbats.qrbats.functionalities.eventcreation.dto.RegisterEventRequest;
import com.qrbats.qrbats.functionalities.eventcreation.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/event")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EventCreationController {

    private final EventService eventService;

    @PostMapping("/create")
    public ResponseEntity<?> createEvent(@RequestBody RegisterEventRequest request) {
        try {
            return ResponseEntity.ok(eventService.createEvent(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateEvent(@RequestBody RegisterEventRequest request) {
        try {
            return ResponseEntity.ok(eventService.updateEvent(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    @PostMapping("/getallevents")
    public ResponseEntity<List<Event>> findAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvent());
    }

    @DeleteMapping("/deletebyid/{eventId}")
    public ResponseEntity<?> deleteById(@PathVariable Integer eventId) {
        try {
            return ResponseEntity.ok(eventService.deleteEvent(eventId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("geteventbyuserid/{userId}")
    public ResponseEntity<List<Event>> findAllEventByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(eventService.getAllEventByUserId(Integer.parseInt(userId)));
    }

    @GetMapping("/geteventbyeventid/{eventId}")
    public ResponseEntity<?> findEventByEventId(@PathVariable Integer eventId){
        try {
            return ResponseEntity.ok(eventService.getEventByEventId(eventId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
