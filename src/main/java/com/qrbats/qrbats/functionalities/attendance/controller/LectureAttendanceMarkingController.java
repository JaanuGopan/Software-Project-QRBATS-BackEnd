package com.qrbats.qrbats.functionalities.attendance.controller;

import com.qrbats.qrbats.functionalities.attendance.dto.LectureAttendanceMarkingRequest;
import com.qrbats.qrbats.functionalities.attendance.service.LectureAttendanceMarkingService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/lectureattendance")
@AllArgsConstructor
@CrossOrigin("*")
public class LectureAttendanceMarkingController {
    @Autowired
    private final LectureAttendanceMarkingService lectureAttendanceMarkingService;

    @PostMapping("/markattendance")
    public ResponseEntity<?> markLectureAttendance(@RequestBody LectureAttendanceMarkingRequest lectureAttendanceMarkingRequest){
        try {
            return ResponseEntity.ok(lectureAttendanceMarkingService.markLectureAttendance(lectureAttendanceMarkingRequest));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/getallattendancebylectureid/{lectureId}")
    public ResponseEntity<?> getAllAttendanceByLectureId(@PathVariable Integer lectureId){
        try {
            return ResponseEntity.ok(lectureAttendanceMarkingService.getAllAttendanceByLectureId(lectureId));
        }catch (RuntimeException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/getallAttendancebymodulecode")
    public ResponseEntity<?> getAllAttendanceByModuleCode(@RequestParam String moduleCode){
        try {
            return ResponseEntity.ok(lectureAttendanceMarkingService.getAllAttendanceByModuleCode(moduleCode));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/getattendancebystudentid/{studentId}")
    public ResponseEntity<?> getAllAttendanceByStudentId(@PathVariable Integer studentId){
        try {
            return ResponseEntity.ok(lectureAttendanceMarkingService.getAllAttendanceByStudentId(studentId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/getallattendancehistorybystudentid/{studentId}")
    public ResponseEntity<?> getAllAttendanceHistoryByStudentId(@PathVariable Integer studentId){
        try {
            return ResponseEntity.ok(lectureAttendanceMarkingService.getAllAttendanceHistoryByStudentId(studentId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
