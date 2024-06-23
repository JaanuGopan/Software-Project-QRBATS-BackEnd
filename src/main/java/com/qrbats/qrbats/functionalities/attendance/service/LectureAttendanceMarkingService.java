package com.qrbats.qrbats.functionalities.attendance.service;

import com.qrbats.qrbats.entity.attendance.AttendanceLecture;
import com.qrbats.qrbats.functionalities.attendance.dto.*;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public interface LectureAttendanceMarkingService {
    AttendanceLecture markLectureAttendance(LectureAttendanceMarkingRequest request);
    AttendanceLecture markLectureAttendanceByLectureId(LectureAttendanceMarkingByLectureIdRequest request);
    List<LectureAttendanceResponse> getAllAttendanceByModuleCode(String moduleCode);

    List<LectureAttendanceResponse> getAllAttendanceByLectureId(Integer lectureId);
    List<AttendanceLecture> getAllAttendanceForOneLecture(String moduleCode , Date day);

    List<LectureAttendanceResponse> getAllAttendanceByStudentId(Integer studentId);

    List<AttendanceLectureHistoryResponse> getAllAttendanceHistoryByStudentId(Integer studentId);

    List<StudentOverallAttendanceResponse> getAllStudentsAttendanceReportByModuleId(Integer moduleId);

    List<LectureWithDateResponse> getAllLectureWithDateForDayLecture(Integer lectureId);
    List<LectureAttendanceResponse> getAllAttendanceForLectureWithDate(Integer lectureId,Date date);


}
