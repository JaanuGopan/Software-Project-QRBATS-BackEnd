package com.qrbats.qrbats.functionalities.attendance.dto;

import lombok.Data;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Data
public class AttendanceListResponse {
    private String studentName;
    private String indexNumber;
    private Date attendedDate;
    private Time attendedTime;
    private boolean attendanceStatus;
}
