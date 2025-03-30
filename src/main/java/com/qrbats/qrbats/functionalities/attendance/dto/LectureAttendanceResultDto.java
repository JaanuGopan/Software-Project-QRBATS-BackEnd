package com.qrbats.qrbats.functionalities.attendance.dto;

import com.qrbats.qrbats.entity.attendance.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LectureAttendanceResultDto {
    private String studentName;
    private AttendanceStatus attendanceStatus;
    private String indexNumber;
    private LocalTime attendedTime;
    private LocalDate attendedDate;
}
