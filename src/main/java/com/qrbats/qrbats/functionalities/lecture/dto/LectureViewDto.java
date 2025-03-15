package com.qrbats.qrbats.functionalities.lecture.dto;

import com.qrbats.qrbats.entity.lecture.LectureStatus;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LectureViewDto {
    private Long id;
    private String name;
    private LocalDate lectureDate;
    private LocalTime lectureStartTime;
    private LocalTime lectureEndTime;
    private String locationName;
    private LectureStatus lectureStatus;
    @Nullable
    private String moduleCode;
    @Nullable
    private Integer moduleId;
    @Nullable
    private Integer createdBy;
}
