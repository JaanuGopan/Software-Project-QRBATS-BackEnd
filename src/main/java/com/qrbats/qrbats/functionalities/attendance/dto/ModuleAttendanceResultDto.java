package com.qrbats.qrbats.functionalities.attendance.dto;

import com.qrbats.qrbats.entity.attendance.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModuleAttendanceResultDto {
    private String studentName;
    private String indexNumber;
    private Integer attendanceCount;
    private Integer notAttendanceCount;
    private Integer finishedLectureCount;
    private Duration attendanceHours;
    private Duration notAttendanceHours;
    private Duration finishedLectureHours;
    private Double attendancePercentage;
    private List<Long> attendedLectureIdList;
}
