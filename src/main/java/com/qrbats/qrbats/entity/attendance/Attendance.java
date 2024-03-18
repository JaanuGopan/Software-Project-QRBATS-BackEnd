package com.qrbats.qrbats.entity.attendance;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Attendance")
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Attendance_id")
    private Integer attendanceId;


    @JoinColumn(name = "Event_id")
    private Integer eventId;

    @JoinColumn(name = "Attendee_id")
    private Integer attendeeId;

    @Column(name = "Attendance_date")
    private LocalDate attendanceDate;

    @Column(name = "Attendance_time")
    private LocalTime attendanceTime;

    @Column(name = "Attendance_locationX")
    private double locationX;

    @Column(name = "Attendance_locationY")
    private double locationY;


}
