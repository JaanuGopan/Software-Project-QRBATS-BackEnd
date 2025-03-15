package com.qrbats.qrbats.functionalities.lecture.controller;

import com.qrbats.qrbats.functionalities.lecture.dto.LectureViewDto;
import com.qrbats.qrbats.functionalities.lecture.service.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/v1/lecture")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LectureController {

    private final LectureService lectureService;

    @GetMapping("/get-all-lecture-for-user")
    public ResponseEntity<List<LectureViewDto>> getAllLecturesForUser() {
        try {
            List<LectureViewDto> lectureDtoList = lectureService.getAllLectureForUser();
            return new ResponseEntity<>(lectureDtoList, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/get-all-lecture-for-user-by-moduleId")
    public ResponseEntity<List<LectureViewDto>> getAllLectureForUserByModuleId(@RequestParam Integer moduleId){
        try {
            List<LectureViewDto> lectureDtoList = lectureService.getAllLectureForUserByModuleId(moduleId);
            return new ResponseEntity<>(lectureDtoList, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/get-all-lecture-by-moduleId-for-student")
    public ResponseEntity<List<LectureViewDto>> getAllLectureForStudentByModuleId(@RequestParam Integer moduleId){
        try {
            List<LectureViewDto> lectureDtoList = lectureService.getAllLectureForStudentByModuleId(moduleId);
            return new ResponseEntity<>(lectureDtoList, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
