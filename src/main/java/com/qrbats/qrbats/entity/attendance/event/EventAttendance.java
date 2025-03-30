package com.qrbats.qrbats.entity.attendance.event;


import com.qrbats.qrbats.entity.attendance.AttendanceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table
public class EventAttendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer attendeeId;
    private Long eventId;
    private LocalDate attendedDate;
    private LocalTime attendedTime;
    private AttendanceStatus attendanceStatus;
}
