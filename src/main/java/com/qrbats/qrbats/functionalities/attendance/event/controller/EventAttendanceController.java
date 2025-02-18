package com.qrbats.qrbats.functionalities.attendance.event.controller;

import com.qrbats.qrbats.functionalities.attendance.lecture.service.LectureAttendanceService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/event")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class EventAttendanceController {

    private final LectureAttendanceService lectureAttendanceService;

    /*@PostMapping("mark-attendance")
    public ResponseEntity<LectureAttendance> markAttandance(@RequestBody AttendanceMarkingDto attendanceMarkingDto){

    }

    @GetMapping("get-all-attendance-by-eventId")
    public ResponseEntity<?> getAllAttendanceByEventId(
            @RequestParam Integer eventId
    ){

    }

    @GetMapping("get-all-attendance-for-student")
    public ResponseEntity<?> getAllAttendanceByStudentId(){

    }*/

}
