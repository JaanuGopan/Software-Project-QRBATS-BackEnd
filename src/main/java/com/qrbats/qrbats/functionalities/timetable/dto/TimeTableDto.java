package com.qrbats.qrbats.functionalities.timetable.dto;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class TimeTableDto {
    @Nullable
    private Integer id;
    private Integer moduleId;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate startingDate;
    private LocalDate validDate;
    @Nullable
    private Integer createdBy;
    private Integer locationId;
}
