package com.qrbats.qrbats.functionalities.attendance;

import com.qrbats.qrbats.entity.attendance.Attendance;

public interface AttendanceMarkingService {
    Attendance markAttendance(AttendaceMarkingRequest attendaceMarkingRequest);
}
