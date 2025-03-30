package com.qrbats.qrbats.entity.attendance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LectureAttendance {
    private LocalDate attendedDate;
    private LocalTime attendedTime;
    private AttendanceStatus attendanceStatus;
}
