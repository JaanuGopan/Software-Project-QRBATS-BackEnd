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


    public boolean verifyLocation(double x,double y){
        double radius = Math.sqrt(Math.pow(6.078787499621265 - x, 2) + Math.pow(80.19183245739939 - y, 2));
        System.out.println(radius);
        if(radius <= 0.002691125381514){
            return true;
        }else {
            return false;
        }
    }
    @PostMapping("markattendance")
    public ResponseEntity<Attendance> markAttandance(
            @RequestBody AttendaceMarkingRequest attendaceMarkingRequest
    ){
        if(verifyLocation(attendaceMarkingRequest.getLocationX(),attendaceMarkingRequest.getLocationY())){
            return ResponseEntity.ok(attendanceMarkingService.markAttendance(attendaceMarkingRequest));
        }else {
            throw new RuntimeException("Location verification failed");
        }
    }
}
