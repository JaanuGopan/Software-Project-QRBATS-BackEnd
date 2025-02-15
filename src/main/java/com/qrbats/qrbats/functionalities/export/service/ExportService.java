package com.qrbats.qrbats.functionalities.export.service;

import com.qrbats.qrbats.authentication.entities.user.User;
import com.qrbats.qrbats.authentication.entities.user.repository.UserRepository;
import com.qrbats.qrbats.entity.attendance.AttendanceEvent;
import com.qrbats.qrbats.entity.attendance.AttendanceLecture;
import com.qrbats.qrbats.entity.module.Module;
import com.qrbats.qrbats.entity.module.ModuleRepository;
import com.qrbats.qrbats.functionalities.attendance.dto.StudentOverallAttendanceResponse;
import com.qrbats.qrbats.functionalities.attendance.service.LectureAttendanceMarkingService;
import com.qrbats.qrbats.functionalities.attendance.service.AttendanceEventService;
import com.qrbats.qrbats.functionalities.attendance.service.AttendanceLectureService;
import com.qrbats.qrbats.entity.event.Event;
import com.qrbats.qrbats.entity.event.EventRepository;
import com.qrbats.qrbats.entity.lecture.Lecture;
import com.qrbats.qrbats.entity.lecture.LectureRepository;
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
public class ExportService {
    @Autowired
    private final AttendanceLectureService attendanceLectureService;
    @Autowired
    private final AttendanceEventService attendanceEventService;
    @Autowired
    private final LectureRepository lectureRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final ModuleService moduleService;
    @Autowired
    private final ModuleRepository moduleRepository;
    @Autowired
    private final LectureAttendanceMarkingService lectureAttendanceMarkingService;


    @Autowired
    final EventRepository eventRepository;


    public ByteArrayInputStream exportLectureAttendance(Integer lectureId) throws IOException {
        Optional<Lecture> lecture = lectureRepository.findById(lectureId);
        if (lecture.isEmpty()) throw new RuntimeException("Lecture Not Found For This Id.");

        List<AttendanceLecture> attendanceLectureList = attendanceLectureService
                .getAllAttendanceByLectureId(lectureId);
        if (attendanceLectureList.isEmpty()) throw new RuntimeException(
                "There Are No Any Attendance Marked For This Lecture.");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(out));

        // Write CSV headers
        String[] lectureName = {"Lecture Name : ", lecture.get().getLectureName()};
        writer.writeNext(lectureName);
        String[] header = {"No", "Student Name", "Index Number", "Attended Date", "Attended Time", "Attendance Status"};
        writer.writeNext(header);
        int number = 1;
        for (AttendanceLecture attendanceLecture : attendanceLectureList) {
            Optional<User> student = userRepository.findById(attendanceLecture.getAttendeeId());
            if (student.isPresent()) {
                String[] row = {Integer.toString(number), student.get().getFirstName() + " " + student.get().getLastName(), student.get().getIndexNumber(),
                        attendanceLecture.getAttendanceDate().toString(),
                        attendanceLecture.getAttendanceTime().toString(),
                        attendanceLecture.getAttendanceStatus() ? "Attended" : "Not Attended"};
                writer.writeNext(row);
                number++;
            }

        }
        List<User> enrolledStudentList = moduleService.getAllEnrolledStudentByModuleCode(lecture.get().getLectureModuleCode());
        for (User student : enrolledStudentList) {
            boolean isAddNotAttendedStudent = true;
            for (AttendanceLecture attendanceLecture : attendanceLectureList) {
                if (attendanceLecture.getAttendeeId().equals(student.getUserId())) {
                    isAddNotAttendedStudent = false;
                }
            }
            if (isAddNotAttendedStudent) {
                String[] row = {Integer.toString(number), student.getFirstName() + " " + student.getLastName(), student.getIndexNumber(), "-", "-", "Not Attended"};
                writer.writeNext(row);
                number++;
            }

        }


        writer.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    public ByteArrayInputStream exportLectureAttendanceByLectureIdAndDate(Integer lectureId, Date date) throws IOException {
        Optional<Lecture> lecture = lectureRepository.findById(lectureId);
        if (lecture.isEmpty()) throw new RuntimeException("Lecture Not Found For This Id.");

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
        int number = 1;
        for (AttendanceLecture attendanceLecture : attendanceLectureList) {
            Optional<User> student = userRepository.findById(attendanceLecture.getAttendeeId());
            if (student.isPresent()) {
                String[] row = {Integer.toString(number), student.get().getFirstName() + " " + student.get().getLastName(), student.get().getIndexNumber(),
                        attendanceLecture.getAttendanceDate().toString(),
                        attendanceLecture.getAttendanceTime().toString(),
                        attendanceLecture.getAttendanceStatus() ? "Attended" : "Not Attended"};
                writer.writeNext(row);
                number++;
            }

        }
        List<User> enrolledStudentList = moduleService.getAllEnrolledStudentByModuleCode(lecture.get().getLectureModuleCode());
        for (User student : enrolledStudentList) {
            boolean isAddNotAttendedStudent = true;
            for (AttendanceLecture attendanceLecture : attendanceLectureList) {
                if (attendanceLecture.getAttendeeId().equals(student.getUserId())) {
                    isAddNotAttendedStudent = false;
                }
            }
            if (isAddNotAttendedStudent) {
                String[] row = {Integer.toString(number), student.getFirstName() + " " + student.getLastName(), student.getIndexNumber(), "-", "-", "Not Attended"};
                writer.writeNext(row);
                number++;
            }

        }


        writer.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

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
            Optional<User> student = userRepository.findById(attendanceEvent.getAttendeeId());
            if (student.isPresent()) {
                String[] row = {number.toString(), student.get().getFirstName() + " " + student.get().getLastName(), student.get().getIndexNumber(),
                        attendanceEvent.getAttendanceDate().toString(), attendanceEvent.getAttendanceTime().toString(),
                        "Attended"};
                writer.writeNext(row);
                number++;
            }

        }
        writer.close();
        return new ByteArrayInputStream(out.toByteArray());

    }

    public ByteArrayInputStream exportOverallStudentAttendance(Integer moduleId) throws IOException {
        Optional<Module> module = moduleRepository.findById(moduleId);
        if (!module.isPresent()) throw new RuntimeException("There is No Any Module for This Id.");


        List<StudentOverallAttendanceResponse> responses = lectureAttendanceMarkingService.getAllStudentsAttendanceReportByModuleId(moduleId);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(out));

        // Write CSV headers
        String[] moduleName = {"Module : ", module.get().getModuleCode().toString()+" - "+module.get().getModuleName()};
        writer.writeNext(moduleName);
        String[] header = {"No", "Student Name", "Index Number", "Attended Lectures", "Total Lectures", "Attendance Percentage"};
        writer.writeNext(header);
        Integer number = 1;
        for (StudentOverallAttendanceResponse response : responses) {
            Optional<User> student = userRepository.findById(response.getStudentId());
            if (student.isPresent()) {
                int totalLectureCount = response.getAttendedLectureCount() + response.getMissedLectureCount();
                String[] row = {number.toString(), student.get().getFirstName() + " " + student.get().getLastName(), student.get().getIndexNumber(),
                        response.getAttendedLectureCount().toString(), String.valueOf(totalLectureCount),
                        String.format("%.2f", response.getAttendancePercentage()) +" %"};
                writer.writeNext(row);
                number++;
            }

        }
        writer.close();
        return new ByteArrayInputStream(out.toByteArray());

    }
}
