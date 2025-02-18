package com.qrbats.qrbats.functionalities.attendance.lecture.controller;

import com.qrbats.qrbats.entity.attendance.lecture.LectureAttendance;
import com.qrbats.qrbats.functionalities.attendance.lecture.dto.LectureAttendanceMarkingDto;
import com.qrbats.qrbats.functionalities.attendance.lecture.dto.LectureAttendanceResultDto;
import com.qrbats.qrbats.functionalities.attendance.lecture.service.LectureAttendanceService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class AttendanceController {
    @Autowired
    private final LectureAttendanceService lectureAttendanceService;

    @PostMapping("/mark-attendance")
    public ResponseEntity<LectureAttendance> markAttendance(@RequestBody LectureAttendanceMarkingDto lectureAttendanceMarkingDto){
        try {
            LectureAttendance lectureAttendance = lectureAttendanceService.markAttendance(lectureAttendanceMarkingDto);
            return new ResponseEntity<>(lectureAttendance, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/get-all-attendance-by-lectureId")
    public ResponseEntity<List<LectureAttendanceResultDto>> getAllAttendanceByLectureId(@RequestParam Long lectureId){
        try {
            List<LectureAttendanceResultDto> lectureAttendanceResultDtoList = lectureAttendanceService.getAllAttendanceForLecture(lectureId);
            return new ResponseEntity<>(lectureAttendanceResultDtoList, HttpStatus.OK);
        } catch (RuntimeException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/get-all-attendance-by-moduleId")
    public ResponseEntity<List<LectureAttendanceResultDto>> getAllAttendanceByModuleCode(@RequestParam Integer moduleId){
        try {
            List<LectureAttendanceResultDto> lectureAttendanceResultDtoList = lectureAttendanceService.getAllAttendanceForModule(moduleId);
            return new ResponseEntity<>(lectureAttendanceResultDtoList, HttpStatus.OK);
        } catch (RuntimeException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/get-all-module-attendance-by-studentId")
    public ResponseEntity<List<LectureAttendanceResultDto>> getAllModuleAttendanceForStudent(@RequestParam Integer moduleId){
        try {
            List<LectureAttendanceResultDto> lectureAttendanceResultDtoList = lectureAttendanceService.getAllModuleAttendanceForStudent(moduleId);
            return new ResponseEntity<>(lectureAttendanceResultDtoList, HttpStatus.OK);
        } catch (RuntimeException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/get-lecture-attendance-for-student")
    public ResponseEntity<LectureAttendanceResultDto> getStudentAttendanceForLecture(Long lectureId){
        try {
            LectureAttendanceResultDto lectureAttendanceResultDtoList = lectureAttendanceService.getStudentAttendanceForLecture(lectureId);
            return new ResponseEntity<>(lectureAttendanceResultDtoList, HttpStatus.OK);
        } catch (RuntimeException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
