package com.qrbats.qrbats.entity.attendance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceData {
    private Map<Integer, Map<Long, LectureAttendance>> attendanceData;
}
