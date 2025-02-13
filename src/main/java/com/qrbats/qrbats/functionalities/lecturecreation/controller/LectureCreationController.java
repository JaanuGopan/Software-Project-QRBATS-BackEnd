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
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LectureCreationController {
    @Autowired
    private final LectureCreationService lectureCreationService;

    @PostMapping("/create-lecture")
    ResponseEntity<?> createLecture(@RequestBody CreateLectureRequest createLectureRequest) {
        try {
            return ResponseEntity.ok(lectureCreationService.createLecture(createLectureRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get-all-lecture-by-userId")
    ResponseEntity<List<Lecture>> getAllLecturesByUserId(@RequestParam Integer userId) {
        return ResponseEntity.ok(lectureCreationService.getAllLectureByUserId(userId));
    }

    @GetMapping("/get-all-lecture-by-module-code")
    ResponseEntity<List<Lecture>> getAllLecturesByModuleCode(@RequestParam String moduleCode) {
        return ResponseEntity.ok(lectureCreationService.getAllLectureByModuleCode(moduleCode));
    }

    @GetMapping("/get-all-lecture-by-venue")
    ResponseEntity<List<Lecture>> getAllLecturesByVenue(@RequestParam String venue) {
        return ResponseEntity.ok(lectureCreationService.getAllLecturesByVenue(venue));
    }

    @GetMapping("/get-all-lecture-by-day")
    ResponseEntity<List<Lecture>> getAllLecturesByDay(@RequestParam String day) {
        return ResponseEntity.ok(lectureCreationService.getAllLecturesByDay(day));
    }

    @PutMapping("/update-lecture/{lectureId}")
    ResponseEntity<?> updateLecture(@PathVariable Integer lectureId, @RequestBody Lecture updateLecture) {
        try {
            return ResponseEntity.ok(lectureCreationService.updateLecture(lectureId, updateLecture));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/delete-lecture/{lectureId}")
    ResponseEntity<?> deleteLecture(@PathVariable Integer lectureId) {
        try {
            lectureCreationService.deleteLecture(lectureId);
            return ResponseEntity.status(200).body(true);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get-all-lectures-by-day-and-venue")
    ResponseEntity<?> getAllLecturesByDayANdVenue(@RequestParam String day,@RequestParam String venue){
        try {
            return ResponseEntity.ok(lectureCreationService.getAllLecturesByDayAndVenue(day,venue));
        }catch (RuntimeException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
