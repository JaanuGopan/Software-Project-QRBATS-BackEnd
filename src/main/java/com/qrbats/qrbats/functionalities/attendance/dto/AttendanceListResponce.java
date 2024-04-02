package com.qrbats.qrbats.functionalities.attendance.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
@Data
public class AttendanceListResponce {
    private String studentName;
    private String indexNumber;
    private LocalDate attendedDate;
    private LocalTime attendedTime;
}
