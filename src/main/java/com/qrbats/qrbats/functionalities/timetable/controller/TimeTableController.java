package com.qrbats.qrbats.functionalities.timetable.controller;

import com.qrbats.qrbats.functionalities.timetable.dto.TimeTableDto;
import com.qrbats.qrbats.functionalities.timetable.service.TimeTableService;
import com.qrbats.qrbats.entity.timetable.TimeTable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/timetables")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TimeTableController {

    private final TimeTableService timeTableService;

    @PostMapping("/create-timetable")
    public ResponseEntity<TimeTable> createTimeTable(@RequestBody TimeTableDto timeTableDto) {
        try {
            TimeTable timeTable = timeTableService.saveTimeTable(timeTableDto);
            return new ResponseEntity<>(timeTable, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error creating timetable: " + e.getMessage());
        }
    }

    @PutMapping("/update-timetable")
    public ResponseEntity<TimeTable> updateTimeTable(@RequestBody TimeTableDto timeTableDto) {
        try {
            TimeTable timeTable = timeTableService.saveTimeTable(timeTableDto);
            return ResponseEntity.ok(timeTable);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error updating timetable: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete-timetable")
    public ResponseEntity<Void> deleteTimeTable(@RequestParam Integer id) {
        try {
            timeTableService.deleteTimeTable(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error deleting timetable: " + e.getMessage());
        }
    }
}
