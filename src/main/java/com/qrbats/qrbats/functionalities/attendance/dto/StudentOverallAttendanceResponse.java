package com.qrbats.qrbats.functionalities.attendance.dto;

import lombok.Data;

@Data
public class StudentOverallAttendanceResponse {
    private Integer studentId;
    private String studentName;
    private String indexNumber;
    private Double attendancePercentage;
    private Integer attendedLectureCount;
    private Integer missedLectureCount;
}
