package com.qrbats.qrbats.functionalities.attendance.service;

import com.qrbats.qrbats.entity.attendance.AttendanceEvent;
import com.qrbats.qrbats.functionalities.attendance.dto.AttendanceEventHistoryResponse;
import com.qrbats.qrbats.functionalities.attendance.dto.AttendanceMarkingRequest;
import com.qrbats.qrbats.functionalities.attendance.dto.AttendanceListResponse;
import com.qrbats.qrbats.functionalities.attendance.dto.AttendanceStudentHistoryResponse;

import java.util.List;

public interface EventAttendanceMarkingService {
    AttendanceEvent markAttendance(AttendanceMarkingRequest attendanceMarkingRequest);

    List<AttendanceListResponse> getAllAttendanceByEventId(Integer eventId);

    List<AttendanceEventHistoryResponse> getAllAttendanceHistoryByStudentId(Integer studentId);

}
