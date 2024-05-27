package com.qrbats.qrbats.entity.attendance;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;


@Data
@AllArgsConstructor
public class AttendanceLecture {
    private Integer attendanceId;
    private Integer lectureId;
    private Integer attendeeId;
    private Date attendanceDate;
    private Time attendanceTime;
    private Boolean attendanceStatus;
}
