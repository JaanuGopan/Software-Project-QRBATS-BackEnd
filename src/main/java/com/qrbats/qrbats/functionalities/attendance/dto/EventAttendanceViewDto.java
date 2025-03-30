package com.qrbats.qrbats.functionalities.attendance.dto;

import com.qrbats.qrbats.entity.attendance.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventAttendanceViewDto {
    private Long id;
    private Integer attendeeId;
    private Long eventId;
    private String eventName;
    private LocalDate attendedDate;
    private LocalTime attendedTime;
    private AttendanceStatus attendanceStatus;
}
