package com.qrbats.qrbats.functionalities.attendance.lecture.dto;

import com.qrbats.qrbats.entity.attendance.AttendanceStatus;
import com.qrbats.qrbats.entity.location.LocationCoordinates;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LectureAttendanceDto {
    @Nullable
    private Long id;
    @Nullable
    private Integer attendeeId;
    private Long lectureId;
    private Long moduleId;
    private Date attendedDate;
    private Time attendedTime;
    private AttendanceStatus attendanceStatus;
    private LocationCoordinates locationCoordinates;
}
