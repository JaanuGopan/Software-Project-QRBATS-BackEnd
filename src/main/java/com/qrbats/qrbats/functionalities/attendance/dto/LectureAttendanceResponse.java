package com.qrbats.qrbats.functionalities.attendance.dto;

import lombok.Data;

import java.sql.Date;
import java.sql.Time;

@Data
public class LectureAttendanceResponse {
    private Integer studentId;
    private String studentIndexNumber;
    private String studentName;
    private Date attendedDate;
    private Time attendedTime;
    private Boolean attendanceStatus;
}
