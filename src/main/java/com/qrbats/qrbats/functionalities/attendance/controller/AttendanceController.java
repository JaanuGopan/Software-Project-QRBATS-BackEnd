package com.qrbats.qrbats.functionalities.attendance.controller;

import com.qrbats.qrbats.entity.attendance.Attendance;
import com.qrbats.qrbats.functionalities.attendance.dto.LectureAttendanceMarkingDto;
import com.qrbats.qrbats.functionalities.attendance.dto.LectureAttendanceResultDto;
import com.qrbats.qrbats.functionalities.attendance.dto.ModuleAttendanceResultDto;
import com.qrbats.qrbats.functionalities.attendance.dto.student.StudentAttendanceResultDto;
import com.qrbats.qrbats.functionalities.attendance.service.LectureAttendanceService;
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
    public ResponseEntity<Attendance> markAttendance(@RequestBody LectureAttendanceMarkingDto lectureAttendanceMarkingDto){
        try {
            Attendance attendance = lectureAttendanceService.markAttendance(lectureAttendanceMarkingDto);
            return new ResponseEntity<>(attendance, HttpStatus.CREATED);
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
    public ResponseEntity<List<ModuleAttendanceResultDto>> getAllAttendanceByModuleCode(@RequestParam Integer moduleId){
        try {
            List<ModuleAttendanceResultDto> lectureAttendanceResultDtoList = lectureAttendanceService.getAllAttendanceForModule(moduleId);
            return new ResponseEntity<>(lectureAttendanceResultDtoList, HttpStatus.OK);
        } catch (RuntimeException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/get-module-attendance-for-student")
    public ResponseEntity<StudentAttendanceResultDto> getModuleAttendanceForStudent(@RequestParam Integer moduleId){
        try {
            StudentAttendanceResultDto studentAttendanceResultDto = lectureAttendanceService.getModuleAttendanceForStudent(moduleId);
            return new ResponseEntity<>(studentAttendanceResultDto, HttpStatus.OK);
        } catch (RuntimeException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
