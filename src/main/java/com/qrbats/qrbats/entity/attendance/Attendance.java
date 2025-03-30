package com.qrbats.qrbats.entity.attendance;

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
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Integer attendeeId;
    private Integer moduleId;
    private Long lectureId;
    private LocalDate attendedDate;
    private LocalTime attendedTime;
    private AttendanceStatus attendanceStatus;
}
