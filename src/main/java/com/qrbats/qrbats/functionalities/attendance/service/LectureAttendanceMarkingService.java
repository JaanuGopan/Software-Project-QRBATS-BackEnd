package com.qrbats.qrbats.functionalities.attendance.service;

import com.qrbats.qrbats.entity.attendance.AttendanceLecture;
import com.qrbats.qrbats.functionalities.attendance.dto.LectureAttendanceMarkingRequest;
import com.qrbats.qrbats.functionalities.attendance.dto.LectureAttendanceResponse;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public interface LectureAttendanceMarkingService {
    AttendanceLecture markLectureAttendance(LectureAttendanceMarkingRequest request);
    List<AttendanceLecture> getAllAttendanceByModuleCode(String moduleCode);

    List<LectureAttendanceResponse> getAllAttendanceByLectureId(Integer lectureId);
    List<AttendanceLecture> getAllAttendanceForOneLecture(String moduleCode , Date day);


}