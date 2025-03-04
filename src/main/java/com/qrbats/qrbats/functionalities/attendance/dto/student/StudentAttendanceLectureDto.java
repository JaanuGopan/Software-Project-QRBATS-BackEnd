package com.qrbats.qrbats.functionalities.attendance.dto.student;

import com.qrbats.qrbats.entity.attendance.AttendanceStatus;
import com.qrbats.qrbats.entity.location.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentAttendanceLectureDto {
    private String lectureName;
    private LocalDate lectureDate;
    private LocalTime lectureStartTime;
    private LocalTime lectureEndTime;
    private String locationName;
    private LocalTime attendedTime;
    private LocalDate attendedDate;
    private AttendanceStatus attendanceStatus;
}
