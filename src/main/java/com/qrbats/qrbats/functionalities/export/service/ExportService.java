package com.qrbats.qrbats.functionalities.export.service;

import com.qrbats.qrbats.authentication.entities.user.User;
import com.qrbats.qrbats.authentication.services.UserService;
import com.qrbats.qrbats.entity.lecture.LectureStatus;
import com.qrbats.qrbats.entity.module.Module;
import com.qrbats.qrbats.entity.lecture.Lecture;
import com.qrbats.qrbats.entity.module_enrollment.ModuleEnrollment;
import com.qrbats.qrbats.functionalities.attendance.lecture.dto.LectureAttendanceResultDto;
import com.qrbats.qrbats.functionalities.attendance.lecture.service.LectureAttendanceService;
import com.qrbats.qrbats.functionalities.lecture.service.LectureService;
import com.qrbats.qrbats.functionalities.module.service.ModuleEnrollmentService;
import com.qrbats.qrbats.functionalities.module.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import com.opencsv.CSVWriter;

@Service
@RequiredArgsConstructor
public class ExportService {
    private final LectureAttendanceService lectureAttendanceService;
    private final LectureService lectureService;
    private final ModuleService moduleService;
    private final ModuleEnrollmentService moduleEnrollmentService;
    private final UserService userService;


    public ByteArrayInputStream exportAttendance(Long lectureId) throws IOException {
        Lecture lecture = lectureService.findLectureById(lectureId);

        List<LectureAttendanceResultDto> lectureAttendanceResultDtoList = lectureAttendanceService
                .getAllAttendanceForLecture(lectureId);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(out));

        // Write CSV headers
        String[] lectureName = {"Lecture Name : ", lecture.getName()};
        writer.writeNext(lectureName);
        String[] header = {"No", "Student Name", "Index Number", "Attended Date", "Attended Time", "Attendance Status"};
        writer.writeNext(header);
        final int[] number = {1};
        for (LectureAttendanceResultDto lectureAttendanceResultDto : lectureAttendanceResultDtoList) {
            String[] row = {
                    Integer.toString(number[0]),
                    lectureAttendanceResultDto.getStudentName(),
                    lectureAttendanceResultDto.getIndexNumber(),
                    lectureAttendanceResultDto.getAttendedDate().toString(),
                    lectureAttendanceResultDto.getAttendedTime().toString(),
                    lectureAttendanceResultDto.getAttendanceStatus().toString()
            };
            writer.writeNext(row);
            number[0]++;
        }
        /*List<ModuleEnrollment> moduleEnrollmentList = moduleEnrollmentService
                .getModuleEnrollmentByModuleId(lecture.getModuleId());
        List<Attendance> attendanceList = attendanceService.getAllAttendanceByLectureId(lectureId);

        List<Integer> notAttendedStudentIdList = new ArrayList<>();

        moduleEnrollmentList.forEach(moduleEnrollment -> {
            AtomicBoolean isAttended = new AtomicBoolean(false);
            attendanceList.forEach(attendance -> {
                if (attendance.getAttendeeId().equals(moduleEnrollment.getStudentId())){
                    isAttended.set(true);
                }
            });
            if (!isAttended.get()) {
               notAttendedStudentIdList.add(moduleEnrollment.getStudentId());
            }
        });

        notAttendedStudentIdList.forEach( studentId -> {
            User user = userService.getUserById(studentId);
            String[] row = {Integer.toString(number[0]), user.getFirstName(), user.getIndexNumber(), "-", "-", AttendanceStatus.ABSENT.name()};
            writer.writeNext(row);
            number[0]++;
        });*/

        writer.close();
        return new ByteArrayInputStream(out.toByteArray());
    }


    public ByteArrayInputStream exportModuleAttendance(Integer moduleId) throws IOException {
        Module module = moduleService.getModuleByModuleId(moduleId);
        List<ModuleEnrollment> moduleEnrollmentList = moduleEnrollmentService.getModuleEnrollmentByModuleId(moduleId);
        lectureService.changeLectureStateForModule(moduleId);
        List<Lecture> lectureList = lectureService.getALlLectureByModuleId(moduleId);
        lectureList.removeIf(
                lecture -> lecture.getLectureStatus().equals(LectureStatus.PENDING)
                        || lecture.getLectureStatus().equals(LectureStatus.CANCELED));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(out));

        // Write CSV headers
        String[] moduleName = {"Module : ", module.getModuleCode() + "-" + module.getModuleName()};
        writer.writeNext(moduleName);
        String[] header = {"No", "Student Name", "Index Number", "Attended Lectures", "Missed Lectures", "Total Lectures", "Attendance Percentage"};
        writer.writeNext(header);
        int number = 1;
        for (ModuleEnrollment moduleEnrollment : moduleEnrollmentList) {
            User student = userService.getUserById(moduleEnrollment.getStudentId());
            int totalLectureCount = lectureList.size();
            int attendedLectureCount = 0;
            for (Lecture lecture : lectureList) {
                if (lectureAttendanceService.checkStudentAttendanceForLecture(student.getUserId(), lecture.getId())) {
                    attendedLectureCount++;
                }
            }

            String[] row = {Integer.toString(number), student.getFirstName(), student.getIndexNumber(),
                    String.valueOf(attendedLectureCount),String.valueOf(totalLectureCount-attendedLectureCount),String.valueOf(totalLectureCount),
                    String.format("%.2f%%", (double) attendedLectureCount / totalLectureCount * 100)};
            writer.writeNext(row);
            number++;

        }
        writer.close();
        return new ByteArrayInputStream(out.toByteArray());

    }
}
