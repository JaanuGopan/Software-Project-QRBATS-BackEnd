package com.qrbats.qrbats.functionalities.lecture.dto;

import com.qrbats.qrbats.entity.lecture.LectureStatus;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LectureDto {
    @Nullable
    private Long id;
    private String name;
    private LocalDate lectureDate;
    private LocalTime lectureStartTime;
    private LocalTime lectureEndTime;
    private Integer locationId;
    private LectureStatus lectureStatus;
    @Nullable
    private Integer moduleId;
    @Nullable
    private Integer createdBy;
}
