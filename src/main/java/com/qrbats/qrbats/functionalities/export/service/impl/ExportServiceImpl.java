package com.qrbats.qrbats.functionalities.export.service.impl;

import com.qrbats.qrbats.authentication.entities.student.Student;
import com.qrbats.qrbats.authentication.entities.student.repository.StudentRepository;
import com.qrbats.qrbats.entity.attendance.AttendanceEvent;
import com.qrbats.qrbats.entity.attendance.AttendanceLecture;
import com.qrbats.qrbats.functionalities.attendance.service.impl.AttendanceLectureService;
import com.qrbats.qrbats.entity.event.Event;
import com.qrbats.qrbats.entity.event.EventRepository;
import com.qrbats.qrbats.entity.lecture.Lecture;
import com.qrbats.qrbats.entity.lecture.LectureRepository;
import com.qrbats.qrbats.functionalities.export.service.ExportService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Optional;

import com.opencsv.CSVWriter;

@Service
@AllArgsConstructor
public class ExportServiceImpl implements ExportService {
    @Autowired
    private final AttendanceLectureService attendanceLectureService;
    @Autowired
    private final LectureRepository lectureRepository;
    @Autowired
    private final StudentRepository studentRepository;


    @Autowired
    final EventRepository eventRepository;


    @Override
    public ByteArrayInputStream exportLectureAttendance(Integer lectureId) throws IOException {
        List<AttendanceLecture> attendanceLectureList = attendanceLectureService.getAllAttendanceByLectureId(lectureId);
        Optional<Lecture> lecture = lectureRepository.findById(lectureId);
        if (!attendanceLectureList.isEmpty() && lecture.isPresent()) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            CSVWriter writer = new CSVWriter(new OutputStreamWriter(out));

            // Write CSV headers
            String[] lectureName = {"Lecture Name : ", lecture.get().getLectureName().toString()};
            writer.writeNext(lectureName);
            String[] header = {"No", "Student Name", "Index Number", "Attended Date", "Attended Time","Attendance Status"};
            writer.writeNext(header);
            Integer number = 1;
            for (AttendanceLecture attendanceLecture : attendanceLectureList) {
                Optional<Student> student = studentRepository.findById(attendanceLecture.getAttendeeId());
                if (student.isPresent()) {
                    String[] row = {number.toString(),student.get().getStudentName(), student.get().getIndexNumber(),
                            attendanceLecture.getAttendanceDate().toString(), attendanceLecture.getAttendanceTime().toString(),
                            attendanceLecture.getAttendanceStatus() ?"Attended":"Not Attended"};
                    writer.writeNext(row);
                    number++;
                }

            }
            writer.close();
            return new ByteArrayInputStream(out.toByteArray());

        } else {
            throw new RuntimeException("Attendance List is Empty.");
        }
    }

    @Override
    public ByteArrayInputStream exportEventAttendance(Integer lectureId) throws IOException {
       /* Optional<Event> event = eventRepository.findById(lectureId);
        if (!event.isPresent()) throw new RuntimeException("There is No Any Lecture or Event for This Id.");

        Optional<List<AttendanceEvent>> attendanceList = attendanceRepository.findAllByEventId(lectureId);
        if (!attendanceList.isPresent()) throw new RuntimeException("There Are No Any Attendance For This Lecture or Event.");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(out));

        // Write CSV headers
        String[] lectureName = {"Lecture Name : ", event.get().getEventName().toString()};
        writer.writeNext(lectureName);
        String[] header = {"No", "Student Name", "Index Number", "Attended Date", "Attended Time","Attendance Status"};
        writer.writeNext(header);
        Integer number = 1;
        for (AttendanceEvent attendanceEvent : attendanceList.get()) {
            Optional<Student> student = studentRepository.findById(attendanceEvent.getAttendeeId());
            if (student.isPresent()) {
                String[] row = {number.toString(),student.get().getStudentName(), student.get().getIndexNumber(),
                        attendanceEvent.getAttendanceDate().toString(), attendanceEvent.getAttendanceTime().toString(),
                        "Attended"};
                writer.writeNext(row);
                number++;
            }

        }
        writer.close();
        return new ByteArrayInputStream(out.toByteArray());*/

        return null;


    }
}
