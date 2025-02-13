package com.qrbats.qrbats.functionalities.attendance.controller;

import com.qrbats.qrbats.functionalities.attendance.dto.LectureAttendanceMarkingByLectureIdRequest;
import com.qrbats.qrbats.functionalities.attendance.dto.LectureAttendanceMarkingRequest;
import com.qrbats.qrbats.functionalities.attendance.service.LectureAttendanceMarkingService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.rmi.server.RemoteServer;
import java.sql.Date;

@RestController
@RequestMapping("/api/v1/lectureattendance")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class LectureAttendanceMarkingController {
    @Autowired
    private final LectureAttendanceMarkingService lectureAttendanceMarkingService;

    @PostMapping("/mark-attendance")
    public ResponseEntity<?> markLectureAttendance(@RequestBody LectureAttendanceMarkingRequest lectureAttendanceMarkingRequest){
        try {
            return ResponseEntity.ok(lectureAttendanceMarkingService.markLectureAttendance(lectureAttendanceMarkingRequest));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/mark-attendance-by-lectureId")
    public ResponseEntity<?> markLectureAttendanceByLectureId(@RequestBody LectureAttendanceMarkingByLectureIdRequest request){
        try {
            return ResponseEntity.ok(lectureAttendanceMarkingService.markLectureAttendanceByLectureId(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/get-all-attendance-by-lectureId/{lectureId}")
    public ResponseEntity<?> getAllAttendanceByLectureId(@PathVariable Integer lectureId){
        try {
            return ResponseEntity.ok(lectureAttendanceMarkingService.getAllAttendanceByLectureId(lectureId));
        }catch (RuntimeException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/get-all-attendance-by-module-code")
    public ResponseEntity<?> getAllAttendanceByModuleCode(@RequestParam String moduleCode){
        try {
            return ResponseEntity.ok(lectureAttendanceMarkingService.getAllAttendanceByModuleCode(moduleCode));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get-attendance-by-studentId/{studentId}")
    public ResponseEntity<?> getAllAttendanceByStudentId(@PathVariable Integer studentId){
        try {
            return ResponseEntity.ok(lectureAttendanceMarkingService.getAllAttendanceByStudentId(studentId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get-all-attendance-history-by-studentId/{studentId}")
    public ResponseEntity<?> getAllAttendanceHistoryByStudentId(@PathVariable Integer studentId){
        try {
            return ResponseEntity.ok(lectureAttendanceMarkingService.getAllAttendanceHistoryByStudentId(studentId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get-all-student-attendance-by-moduleId/{moduleId}")
    public ResponseEntity<?> getAllStudentsAttendanceReportByModuleId(@PathVariable Integer moduleId){
        try {
            return ResponseEntity.ok(lectureAttendanceMarkingService.getAllStudentsAttendanceReportByModuleId(moduleId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @GetMapping("/get-all-lecture-with-date-for-day-lecture/{lectureId}")
    public ResponseEntity<?> getAllLectureWithDateForDayLecture(@PathVariable Integer lectureId){
        try {
            return ResponseEntity.ok(lectureAttendanceMarkingService.getAllLectureWithDateForDayLecture(lectureId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get-all-attendance-by-lectureId-and-date")
    public ResponseEntity<?> getAllAttendanceByLectureIdAndDate(@RequestParam Integer lectureId, @RequestParam Date date){
        try {
            return ResponseEntity.ok(lectureAttendanceMarkingService.getAllAttendanceForLectureWithDate(lectureId,date));
        }catch (RuntimeException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
