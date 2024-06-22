package com.qrbats.qrbats.functionalities.attendance.controller;

import com.qrbats.qrbats.functionalities.attendance.dto.*;
import com.qrbats.qrbats.functionalities.attendance.service.EventAttendanceMarkingService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
@AllArgsConstructor
@CrossOrigin("*")
public class EventAttendanceMarkingController {
    private final EventAttendanceMarkingService eventAttendanceMarkingService;

    @PostMapping("markattendance")
    public ResponseEntity<?> markAttandance(
            @RequestBody AttendanceMarkingRequest attendaceMarkingRequest
    ){
        try {
            return ResponseEntity.ok(eventAttendanceMarkingService.markAttendance(attendaceMarkingRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("getallattendancebyeventid/{eventId}")
    public ResponseEntity<?> getAllAttendanceByEventId(
            @PathVariable Integer eventId
    ){
        try {
            return ResponseEntity.ok(eventAttendanceMarkingService.getAllAttendanceByEventId(eventId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("getallattendancebystudentid/{studentId}")
    public ResponseEntity<?> getAllAttendanceByStudentId(@PathVariable Integer studentId){
        try {
            return ResponseEntity.ok(eventAttendanceMarkingService.getAllAttendanceHistoryByStudentId(studentId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
