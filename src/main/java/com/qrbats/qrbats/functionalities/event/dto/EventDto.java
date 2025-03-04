package com.qrbats.qrbats.functionalities.event.dto;

import com.qrbats.qrbats.entity.lecture.LectureStatus;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class EventDto {
    @Nullable
    private Long id;
    private String eventName;
    private LocalDate eventDate;
    private Integer locationId;
    private LocalTime eventStartTime;
    private LocalTime eventEndTime;
    @Nullable
    private Integer createdBy;
}
