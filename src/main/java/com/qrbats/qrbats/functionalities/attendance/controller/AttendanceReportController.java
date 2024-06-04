package com.qrbats.qrbats.functionalities.attendance.controller;

import com.qrbats.qrbats.authentication.entities.student.Student;
import com.qrbats.qrbats.entity.attendance.AttendanceLecture;
import com.qrbats.qrbats.functionalities.attendance.service.AttendanceReportService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/report")
@AllArgsConstructor
@CrossOrigin("*")
public class AttendanceReportController {
    @Autowired
    private final AttendanceReportService attendanceReportService;
    @GetMapping("/getallstudentsbydepartmentidandsemester")
    public ResponseEntity<List<Student>> getAllStudentsByDepartmentIdAndSemester(
            @RequestParam Integer deptId, @RequestParam Integer sem) {
        return ResponseEntity.ok(
                attendanceReportService.getAllStudentsByDepartmentIdAndSemester(deptId, sem)
        );
    }

    @GetMapping("/getallAttendancebylectureid")
    public ResponseEntity<List<AttendanceLecture>> getAllAttendanceByLectureId(@RequestParam Integer lectureID){

        System.out.println("hello jaanu");
        return ResponseEntity.ok(attendanceReportService.getAllAttendanceByLectureId(lectureID));
    }
}
