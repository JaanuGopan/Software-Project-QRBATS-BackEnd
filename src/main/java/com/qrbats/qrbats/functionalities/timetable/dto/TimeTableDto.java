package com.qrbats.qrbats.functionalities.timetable.dto;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;
import java.sql.Time;
import java.time.DayOfWeek;

@Data
@AllArgsConstructor
public class TimeTableDto {
    @Nullable
    private Integer id;
    private Integer moduleId;
    private DayOfWeek dayOfWeek;
    private Time startTime;
    private Time endTime;
    private Date startingDate;
    private Date validDate;
    @Nullable
    private Integer createdBy;
    private Integer locationId;
}
