package com.qrbats.qrbats.functionalities.attendance.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AttendanceStudentHistoryResponse {
    private String eventName;
    private LocalDate attendedDate;
    private LocalTime attendedTime;
}
