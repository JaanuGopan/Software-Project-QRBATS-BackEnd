package com.qrbats.qrbats.functionalities.attendance.controller;

import com.qrbats.qrbats.entity.attendance.Attendance;
import com.qrbats.qrbats.functionalities.attendance.dto.*;
import com.qrbats.qrbats.functionalities.attendance.service.AttendanceMarkingService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
@AllArgsConstructor
@CrossOrigin("*")
public class AttendanceMarkingController {
    private final AttendanceMarkingService attendanceMarkingService;

    @PostMapping("markattendance")
    public ResponseEntity<Attendance> markAttandance(
            @RequestBody AttendanceMarkingRequest attendaceMarkingRequest
    ){
        return ResponseEntity.ok(attendanceMarkingService.markAttendance(attendaceMarkingRequest));
    }

    @PostMapping("getallattendancebyeventid")
    public ResponseEntity<List<AttendanceListResponce>> getAllAttendanceByEventId(
            @RequestBody AttendanceListRequest attendanceListRequest
    ){
        return ResponseEntity.ok(attendanceMarkingService.getAllAttendanceByEventId(attendanceListRequest.getEventId()));
    }

    @PostMapping("getallattendancebystudentid")
    public ResponseEntity<List<AttendanceStudentHistoryResponse>> getAllAttendanceByStudentId(@RequestBody AttendanceStudentHistoryRequest request){
        return ResponseEntity.ok(attendanceMarkingService.getAllAttendanceByStudentId(request.getStudentId()));
    }

}
