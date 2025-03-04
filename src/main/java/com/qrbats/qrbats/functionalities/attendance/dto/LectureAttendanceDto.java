package com.qrbats.qrbats.functionalities.attendance.dto;

import com.qrbats.qrbats.entity.attendance.AttendanceStatus;
import com.qrbats.qrbats.entity.location.LocationCoordinates;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

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
    private LocalDate attendedDate;
    private LocalTime attendedTime;
    private AttendanceStatus attendanceStatus;
    private LocationCoordinates locationCoordinates;
}
