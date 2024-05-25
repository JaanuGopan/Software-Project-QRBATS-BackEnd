package com.qrbats.qrbats.functionalities.attendance.service;

import com.qrbats.qrbats.entity.attendance.Attendance;
import com.qrbats.qrbats.functionalities.attendance.dto.AttendanceMarkingRequest;
import com.qrbats.qrbats.functionalities.attendance.dto.AttendanceListResponce;
import com.qrbats.qrbats.functionalities.attendance.dto.AttendanceStudentHistoryResponse;

import java.util.List;

public interface AttendanceMarkingService {
    Attendance markAttendance(AttendanceMarkingRequest attendaceMarkingRequest);

    List<AttendanceListResponce> getAllAttendanceByEventId(Integer eventId);

    List<AttendanceStudentHistoryResponse> getAllAttendanceByStudentId(Integer studentId);

}
