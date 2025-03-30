package com.qrbats.qrbats.functionalities.timetable.controller;

import com.qrbats.qrbats.entity.lecture.Lecture;
import com.qrbats.qrbats.functionalities.timetable.dto.TimeTableDto;
import com.qrbats.qrbats.functionalities.timetable.service.TimeTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/timetables")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TimeTableController {

    private final TimeTableService timeTableService;

    @PostMapping("/create-timetable")
    public ResponseEntity<List<Lecture>> createTimeTable(@RequestBody TimeTableDto timeTableDto) {
        try {
            List<Lecture> createdLectures = timeTableService.saveTimeTable(timeTableDto);
            return new ResponseEntity<>(createdLectures, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error creating timetable: " + e.getMessage());
        }
    }
}
