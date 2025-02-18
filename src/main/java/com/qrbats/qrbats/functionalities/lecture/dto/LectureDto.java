package com.qrbats.qrbats.functionalities.lecture.dto;

import com.qrbats.qrbats.entity.lecture.LectureStatus;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;
import java.sql.Time;

@Data
@AllArgsConstructor
public class LectureDto {
    @Nullable
    private Long id;
    private String name;
    private Date lectureDate;
    private Time lectureStartTime;
    private Time lectureEndTime;
    private Integer locationId;
    private LectureStatus lectureStatus;
    @Nullable
    private Integer moduleId;
    @Nullable
    private Integer createdBy;
}
