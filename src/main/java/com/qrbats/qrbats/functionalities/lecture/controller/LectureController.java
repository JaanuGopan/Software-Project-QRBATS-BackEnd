package com.qrbats.qrbats.functionalities.lecture.controller;

import com.qrbats.qrbats.entity.lecture.Lecture;
import com.qrbats.qrbats.functionalities.lecture.dto.LectureConflictDto;
import com.qrbats.qrbats.functionalities.lecture.dto.LectureDto;
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

    @PostMapping("/create-single-lecture")
    public ResponseEntity<Lecture> createSingleLecture(@RequestBody LectureDto lectureDto) {
        try {
            Lecture lecture = lectureService.saveSingleLecture(lectureDto);
            return new ResponseEntity<>(lecture, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/update-single-lecture")
    public ResponseEntity<Lecture> updateSingleLecture(@RequestBody LectureDto lectureDto) {
        try {
            Lecture lecture = lectureService.saveSingleLecture(lectureDto);
            return new ResponseEntity<>(lecture, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/delete-lecture")
    public ResponseEntity<Void> deleteSingleLecture(@RequestParam Long id) {
        try {
            lectureService.deleteLectureById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/check-lecture-conflict")
    public boolean checkLectureConflict(@RequestBody LectureConflictDto lectureConflictDto) {
        return lectureService.checkLectureTimeConflict(lectureConflictDto);
    }

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

    @GetMapping("/get-all-finished-lecture-for-user-by-moduleId")
    public ResponseEntity<List<LectureViewDto>> getAllFinishedLectureForUserByModuleId(@RequestParam Integer moduleId){
        try {
            List<LectureViewDto> lectureDtoList = lectureService.getAllFinishedLectureForUserByModuleId(moduleId);
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

    @GetMapping("/get-all-event-for-user")
    public ResponseEntity<List<LectureViewDto>> getAllEventForUser(){
        try {
            List<LectureViewDto> lectureDtoList = lectureService.getAllEventsForUser();
            return new ResponseEntity<>(lectureDtoList, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/get-lecture-by-lectureId")
    public ResponseEntity<LectureViewDto> getLectureById(@RequestParam Long lectureId){
        try {
            LectureViewDto lectureViewDto = lectureService.getLectureById(lectureId);
            return new ResponseEntity<>(lectureViewDto, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
