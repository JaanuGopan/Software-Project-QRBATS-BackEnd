package com.qrbats.qrbats.functionalities.attendance.lecture.dto.student;

import com.qrbats.qrbats.entity.attendance.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Time;
import java.util.Date;

@Data
@AllArgsConstructor
public class StudentAttendanceLectureDto {
    private String lectureName;
    private Time attendedTime;
    private Date attendedDate;
    private AttendanceStatus attendanceStatus;
}
