package com.qrbats.qrbats.functionalities.attendance.lecture.dto;

import com.qrbats.qrbats.entity.attendance.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LectureAttendanceResultDto {
    private String studentName;
    private AttendanceStatus attendanceStatus;
    private String indexNumber;
    private Time attendedTime;
    private Date attendedDate;
}
