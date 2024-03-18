package com.qrbats.qrbats.functionalities.eventcreation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/event")
@CrossOrigin("http://localhost:3000")
@RequiredArgsConstructor
public class EventCreationController {

    private final EventCreationService service;


    @PostMapping("/create")
    public void createEvent(@RequestBody RegisterEventRequest request) {
        service.createEvent(request);
    }




}
