package com.qrbats.qrbats.entity.attendance;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceEvent {
    private Integer attendanceId;
    private Integer eventId;
    private Integer attendeeId;
    private Date attendanceDate;
    private Time attendanceTime;
    private Boolean attendanceStatus;
}
