package com.qrbats.qrbats.functionalities.export.service.impl;

import com.qrbats.qrbats.authentication.entities.student.Student;
import com.qrbats.qrbats.authentication.entities.student.repository.StudentRepository;
import com.qrbats.qrbats.entity.attendance.AttendanceEvent;
import com.qrbats.qrbats.entity.attendance.AttendanceLecture;
import com.qrbats.qrbats.functionalities.attendance.service.impl.AttendanceEventService;
import com.qrbats.qrbats.functionalities.attendance.service.impl.AttendanceLectureService;
import com.qrbats.qrbats.entity.event.Event;
import com.qrbats.qrbats.entity.event.EventRepository;
import com.qrbats.qrbats.entity.lecture.Lecture;
import com.qrbats.qrbats.entity.lecture.LectureRepository;
import com.qrbats.qrbats.functionalities.export.service.ExportService;
import com.qrbats.qrbats.functionalities.module_creation.services.ModuleService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

import com.opencsv.CSVWriter;

@Service
@AllArgsConstructor
public class ExportServiceImpl implements ExportService {
    @Autowired
    private final AttendanceLectureService attendanceLectureService;
    @Autowired
    private final AttendanceEventService attendanceEventService;
    @Autowired
    private final LectureRepository lectureRepository;
    @Autowired
    private final StudentRepository studentRepository;
    @Autowired
    private final ModuleService moduleService;


    @Autowired
    final EventRepository eventRepository;


    @Override
    public ByteArrayInputStream exportLectureAttendance(Integer lectureId) throws IOException {
        Optional<Lecture> lecture = lectureRepository.findById(lectureId);
        if (!lecture.isPresent()) throw new RuntimeException("Lecture Not Found For This Id.");

        List<AttendanceLecture> attendanceLectureList = attendanceLectureService
                .getAllAttendanceByLectureId(lectureId);
        if (attendanceLectureList.isEmpty()) throw new RuntimeException(
                "There Are No Any Attendance Marked For This Lecture.");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(out));

        // Write CSV headers
        String[] lectureName = {"Lecture Name : ", lecture.get().getLectureName().toString()};
        writer.writeNext(lectureName);
        String[] header = {"No", "Student Name", "Index Number", "Attended Date", "Attended Time", "Attendance Status"};
        writer.writeNext(header);
        Integer number = 1;
        for (AttendanceLecture attendanceLecture : attendanceLectureList) {
            Optional<Student> student = studentRepository.findById(attendanceLecture.getAttendeeId());
            if (student.isPresent()) {
                String[] row = {number.toString(), student.get().getStudentName(), student.get().getIndexNumber(),
                        attendanceLecture.getAttendanceDate().toString(),
                        attendanceLecture.getAttendanceTime().toString(),
                        attendanceLecture.getAttendanceStatus() ? "Attended" : "Not Attended"};
                writer.writeNext(row);
                number++;
            }

        }
        List<Student> enrolledStudentList = moduleService.getAllEnrolledStudentByModuleCode(lecture.get().getLectureModuleCode());
        for (Student student : enrolledStudentList) {
            boolean isAddNotAttendedStudent = true;
            for (AttendanceLecture attendanceLecture : attendanceLectureList) {
                if (attendanceLecture.getAttendeeId().equals(student.getStudentId())) {
                    isAddNotAttendedStudent = false;
                }
            }
            if (isAddNotAttendedStudent) {
                String[] row = {number.toString(), student.getStudentName(), student.getIndexNumber(), "-", "-", "Not Attended"};
                writer.writeNext(row);
                number++;
            }

        }


        writer.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public ByteArrayInputStream exportLectureAttendanceByLectureIdAndDate(Integer lectureId, Date date) throws IOException {
        Optional<Lecture> lecture = lectureRepository.findById(lectureId);
        if (!lecture.isPresent()) throw new RuntimeException("Lecture Not Found For This Id.");

        List<AttendanceLecture> attendanceLectureList = attendanceLectureService
                .getAttendanceByLectureIdAndDate(lectureId,date);
        if (attendanceLectureList.isEmpty()) throw new RuntimeException(
                "There Are No Any Attendance Marked For This Lecture.");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(out));

        // Write CSV headers
        String[] lectureName = {"Lecture Name : ", lecture.get().getLectureModuleCode()+"_"+date.toString()};
        writer.writeNext(lectureName);
        String[] header = {"No", "Student Name", "Index Number", "Attended Date", "Attended Time", "Attendance Status"};
        writer.writeNext(header);
        Integer number = 1;
        for (AttendanceLecture attendanceLecture : attendanceLectureList) {
            Optional<Student> student = studentRepository.findById(attendanceLecture.getAttendeeId());
            if (student.isPresent()) {
                String[] row = {number.toString(), student.get().getStudentName(), student.get().getIndexNumber(),
                        attendanceLecture.getAttendanceDate().toString(),
                        attendanceLecture.getAttendanceTime().toString(),
                        attendanceLecture.getAttendanceStatus() ? "Attended" : "Not Attended"};
                writer.writeNext(row);
                number++;
            }

        }
        List<Student> enrolledStudentList = moduleService.getAllEnrolledStudentByModuleCode(lecture.get().getLectureModuleCode());
        for (Student student : enrolledStudentList) {
            boolean isAddNotAttendedStudent = true;
            for (AttendanceLecture attendanceLecture : attendanceLectureList) {
                if (attendanceLecture.getAttendeeId().equals(student.getStudentId())) {
                    isAddNotAttendedStudent = false;
                }
            }
            if (isAddNotAttendedStudent) {
                String[] row = {number.toString(), student.getStudentName(), student.getIndexNumber(), "-", "-", "Not Attended"};
                writer.writeNext(row);
                number++;
            }

        }


        writer.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public ByteArrayInputStream exportEventAttendance(Integer eventId) throws IOException {


        Optional<Event> event = eventRepository.findById(eventId);
        if (!event.isPresent()) throw new RuntimeException("There is No Any Lecture or Event for This Id.");

        List<AttendanceEvent> attendanceList = attendanceEventService.getAllAttendanceByEventId(eventId);
        if (attendanceList == null)
            throw new RuntimeException("There Are No Any Attendance For This Lecture or Event.");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(out));

        // Write CSV headers
        String[] eventName = {"EventName Name : ", event.get().getEventName().toString()};
        writer.writeNext(eventName);
        String[] header = {"No", "Student Name", "Index Number", "Attended Date", "Attended Time", "Attendance Status"};
        writer.writeNext(header);
        Integer number = 1;
        for (AttendanceEvent attendanceEvent : attendanceList) {
            Optional<Student> student = studentRepository.findById(attendanceEvent.getAttendeeId());
            if (student.isPresent()) {
                String[] row = {number.toString(), student.get().getStudentName(), student.get().getIndexNumber(),
                        attendanceEvent.getAttendanceDate().toString(), attendanceEvent.getAttendanceTime().toString(),
                        "Attended"};
                writer.writeNext(row);
                number++;
            }

        }
        writer.close();
        return new ByteArrayInputStream(out.toByteArray());

    }
}
