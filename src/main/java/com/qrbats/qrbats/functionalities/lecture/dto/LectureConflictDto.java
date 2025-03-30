package com.qrbats.qrbats.functionalities.lecture.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LectureConflictDto {
    private LocalDate lectureDate;
    private Integer locationId;
    private LocalTime startTime;
    private LocalTime endTime;
}
