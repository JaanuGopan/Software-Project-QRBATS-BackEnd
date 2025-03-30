package com.qrbats.qrbats.functionalities.timetable.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DaySchedule {
    private DayOfWeek day;
    private List<TimeSlot> timeSlots;
}
