package com.qrbats.qrbats.functionalities.attendance.controller;

import com.qrbats.qrbats.entity.attendance.Attendance;
import com.qrbats.qrbats.entity.attendance.event.EventAttendance;
import com.qrbats.qrbats.functionalities.attendance.dto.*;
import com.qrbats.qrbats.functionalities.attendance.dto.student.StudentAttendanceResultDto;
import com.qrbats.qrbats.functionalities.attendance.service.EventAttendanceService;
import com.qrbats.qrbats.functionalities.attendance.service.LectureAttendanceService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AttendanceController {
    private final LectureAttendanceService lectureAttendanceService;
    private final EventAttendanceService eventAttendanceService;

    @PostMapping("/mark-attendance-for-lecture")
    public ResponseEntity<Attendance> markAttendance(@RequestBody LectureAttendanceMarkingDto lectureAttendanceMarkingDto){
        try {
            Attendance attendance = lectureAttendanceService.markAttendance(lectureAttendanceMarkingDto);
            return new ResponseEntity<>(attendance, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/mark-attendance-for-event")
    public ResponseEntity<EventAttendance> markEventAttendance(@RequestBody EventAttendanceMarkingDto eventAttendanceMarkingDto){
        try {
            EventAttendance attendance = eventAttendanceService.markEventAttendance(eventAttendanceMarkingDto);
            return new ResponseEntity<>(attendance, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/get-all-attendance-by-lectureId")
    public ResponseEntity<List<LectureAttendanceResultDto>> getAllAttendanceByLectureId(@RequestParam Long lectureId){
        try {
            List<LectureAttendanceResultDto> lectureAttendanceResultDtoList = lectureAttendanceService
                    .getAllAttendanceForLecture(lectureId);
            return new ResponseEntity<>(lectureAttendanceResultDtoList, HttpStatus.OK);
        } catch (IllegalArgumentException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/get-all-attendance-for-event")
    public ResponseEntity<List<LectureAttendanceResultDto>> getAllAttendanceForEvent(@RequestParam Long eventId){
        try {
            List<LectureAttendanceResultDto> eventAttendanceResultDtoList = eventAttendanceService
                    .getAllAttendanceForEvent(eventId);
            return new ResponseEntity<>(eventAttendanceResultDtoList, HttpStatus.OK);
        } catch (IllegalArgumentException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/get-all-event-attendance-for-student")
    public ResponseEntity<List<EventAttendanceViewDto>> getAllEventAttendanceForStudent(){
        try {
            List<EventAttendanceViewDto> eventAttendanceViewDtoList = eventAttendanceService
                    .getAllAttendanceForStudent();
            return new ResponseEntity<>(eventAttendanceViewDtoList, HttpStatus.OK);
        } catch (IllegalArgumentException e){
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
