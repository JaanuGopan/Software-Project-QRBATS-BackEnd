package com.qrbats.qrbats.functionalities.lecturecreation.controller;

import com.qrbats.qrbats.entity.lecture.Lecture;
import com.qrbats.qrbats.functionalities.lecturecreation.dto.CreateLectureRequest;
import com.qrbats.qrbats.functionalities.lecturecreation.service.LectureCreationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/lecture")
@CrossOrigin("*")
@RequiredArgsConstructor
public class LectureCreationController {
    @Autowired
    private final LectureCreationService lectureCreationService;

    @PostMapping("/createlecture")
    ResponseEntity<List<Lecture>> createLecture(@RequestBody CreateLectureRequest createLectureRequest) {
        return ResponseEntity.ok(lectureCreationService.createLecture(createLectureRequest));
    }

    @GetMapping("/getalllecturebyuserid/{userId}")
    ResponseEntity<List<Lecture>> getAllLecturesByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(lectureCreationService.getAllLectureByUserId(userId));
    }

    @GetMapping("/getalllecturebymodulecode")
    ResponseEntity<List<Lecture>> getAllLecturesByUserId(@RequestParam String moduleCode) {
        return ResponseEntity.ok(lectureCreationService.getAllLectureByModuleCode(moduleCode));
    }

    @GetMapping("/getalllecturebyvenue")
    ResponseEntity<List<Lecture>> getAllLecturesByVenue(@RequestParam String venue) {
        return ResponseEntity.ok(lectureCreationService.getAllLecturesByVenue(venue));
    }

    @GetMapping("/getalllecturebyday")
    ResponseEntity<List<Lecture>> getAllLecturesByDay(@RequestParam String day) {
        return ResponseEntity.ok(lectureCreationService.getAllLecturesByDay(day));
    }

    @PutMapping("/updatelecture/{lectureId}")
    ResponseEntity<?> updateLecture(@PathVariable Integer lectureId, @RequestBody Lecture updateLecture) {
        try {
            return ResponseEntity.ok(lectureCreationService.updateLecture(lectureId, updateLecture));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/deletelecture/{lectureId}")
    ResponseEntity<Void> deleteLecture(@PathVariable Integer lectureId) {
        lectureCreationService.deleteLecture(lectureId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getalllecturesbydayandvenue")
    ResponseEntity<?> getAllLecturesByDayANdVenue(@RequestParam String day,@RequestParam String venue){
        try {
            return ResponseEntity.ok(lectureCreationService.getAllLecturesByDayAndVenue(day,venue));
        }catch (RuntimeException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
