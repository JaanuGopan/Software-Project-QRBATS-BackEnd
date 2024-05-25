package com.qrbats.qrbats.functionalities.attendance.service.impl;

import com.qrbats.qrbats.authentication.entities.student.Student;
import com.qrbats.qrbats.authentication.entities.student.repository.StudentRepository;
import com.qrbats.qrbats.entity.attendance.AttendanceLecture;
import com.qrbats.qrbats.entity.attendance.AttendanceLectureService;
import com.qrbats.qrbats.entity.event.Event;
import com.qrbats.qrbats.entity.event.EventRepository;
import com.qrbats.qrbats.functionalities.attendance.service.AttendanceReportService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AttendanceReportServiceImpl implements AttendanceReportService {
    @Autowired
    private final StudentRepository studentRepository;
    @Autowired
    private final AttendanceLectureService attendanceLectureService;
    @Autowired
    private final EventRepository eventRepository;

    @Override
    public List<Student> getAllStudentsByDepartmentIdAndSemester(Integer deptID, Integer sem) {
        Optional<List<Student>> students = studentRepository.findAllByDepartmentIdAndCurrentSemester(deptID, sem);
        if (students.isPresent()) {
            return students.get();
        } else {
            throw new RuntimeException("Failed to get students.");
        }
    }

    @Override
    public List<AttendanceLecture> getAllAttendanceByLectureId(String lectureId) {
        Optional<Event> event = eventRepository.findById(Integer.parseInt(lectureId));

        if (event.isPresent()) {
            List<AttendanceLecture> attendanceLectureList = attendanceLectureService.getAllAttendanceByLectureId(lectureId);
            return attendanceLectureList;
        }else {
            throw new RuntimeException("There is no Lecture found for given lecture id.");
        }
    }
}
