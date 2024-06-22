package com.qrbats.qrbats.functionalities.eventcreation.controller;

import com.qrbats.qrbats.entity.event.Event;
import com.qrbats.qrbats.functionalities.eventcreation.dto.DeleteByIdRequest;
import com.qrbats.qrbats.functionalities.eventcreation.dto.GetAllEventByModuleCodeRequest;
import com.qrbats.qrbats.functionalities.eventcreation.dto.GetAllEventsByUserIdRequest;
import com.qrbats.qrbats.functionalities.eventcreation.dto.RegisterEventRequest;
import com.qrbats.qrbats.functionalities.eventcreation.services.impl.EventServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/event")
@CrossOrigin("*")
@RequiredArgsConstructor
public class EventCreationController {

    private final EventServiceImpl eventService;


    @PostMapping("/create")
    public ResponseEntity<Event> createEvent(@RequestBody RegisterEventRequest request) {
        return ResponseEntity.ok(eventService.createEvent(request));
    }

    @GetMapping("/getalleventbymodulecode")
    public ResponseEntity<List<Event>> getAllEventByModuleCode(
            @RequestParam String moduleCode) {
        return ResponseEntity.ok(eventService.getAllEventByModuleCode(moduleCode));
    }

    @PostMapping("/getallevents")
    public ResponseEntity<List<Event>> findAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvent());
    }

    @DeleteMapping("/deletebyid/{eventId}")
    public void deleteById(@PathVariable Integer eventId) {
        eventService.deleteEvent(eventId);
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
