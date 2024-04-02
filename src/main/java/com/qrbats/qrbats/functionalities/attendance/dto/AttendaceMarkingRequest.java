package com.qrbats.qrbats.functionalities.attendance.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttendaceMarkingRequest {
    private Integer eventID;
    private Integer attendeeID;
    private LocalDate attendanceDate;
    private LocalTime attendanceTime;
    private double locationGPSLatitude;
    private double locationGPSLongitude;
}
