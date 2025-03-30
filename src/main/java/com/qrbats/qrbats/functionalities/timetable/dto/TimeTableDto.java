package com.qrbats.qrbats.functionalities.timetable.dto;


import com.qrbats.qrbats.functionalities.timetable.dto.schedule.DaySchedule;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class TimeTableDto {
    @Nullable
    private Integer id;
    private Integer moduleId;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<DayOfWeek> dayList;
    private List<DaySchedule> dayScheduleList;
    private Map<DayOfWeek, List<LocalDate>> lectureDatesForDayList;
    @Nullable
    private Integer createdBy;
}
