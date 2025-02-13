package com.qrbats.qrbats.functionalities.export.controller;

import com.qrbats.qrbats.functionalities.export.service.ExportService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Date;

@RestController
@RequestMapping("api/v1/export")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExportController {
    @Autowired
    private final ExportService exportService;

    @GetMapping("/get-csv/{lectureId}")
    public ResponseEntity<?> getLectureAttendanceReport(@PathVariable Integer lectureId) throws IOException {
        try {
            ByteArrayInputStream in = exportService.exportLectureAttendance(lectureId);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=lecture_"+lectureId.toString()+".csv");
            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM).body(new InputStreamResource(in));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    @GetMapping("/download-attendance-by-lectureId-and-date")
    public ResponseEntity<?> downloadAttendanceReportByLectureIdAndDate(@RequestParam Integer lectureId, @RequestParam Date date) throws IOException {
        try {
            ByteArrayInputStream in = exportService.exportLectureAttendanceByLectureIdAndDate(lectureId,date);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=lecture_"+lectureId.toString()+"_"+date.toString()+".csv");
            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM).body(new InputStreamResource(in));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/get-event-report/{eventId}")
    public ResponseEntity<?> getEventAttendanceReport(@PathVariable Integer eventId) throws IOException {
        try {
            ByteArrayInputStream in = exportService.exportEventAttendance(eventId);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=event_"+eventId.toString()+".csv");
            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM).body(new InputStreamResource(in));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    @GetMapping("/get-students-overall-report/{moduleId}")
    public ResponseEntity<?> getStudentOverallAttendanceReport(@PathVariable Integer moduleId) throws IOException {
        try {
            ByteArrayInputStream in = exportService.exportOverallStudentAttendance(moduleId);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=module_"+moduleId.toString()+"_students_report.csv");
            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM).body(new InputStreamResource(in));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
