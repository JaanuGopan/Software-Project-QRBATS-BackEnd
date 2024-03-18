package com.qrbats.qrbats.functionalities.attendance;

import com.qrbats.qrbats.entity.attendance.Attendance;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/attendance")
@AllArgsConstructor
@CrossOrigin("http://localhost:3000/")
public class AttendanceMarkingController {
    private final AttendanceMarkingService attendanceMarkingService;
    @PostMapping("markattendance")
    public ResponseEntity<Attendance> markAttandance(
            @RequestBody AttendaceMarkingRequest attendaceMarkingRequest
    ){
        return ResponseEntity.ok(attendanceMarkingService.markAttendance(attendaceMarkingRequest));
    }
}
