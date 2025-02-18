package com.qrbats.qrbats.functionalities.attendance.lecture.dto.student;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StudentAttendanceHistoryDto {
    private List<StudentAttendanceLectureDto> studentAttendancesLectureList;
    private Integer attendanceCount;
    private Integer missedAttendanceCount;
    private Double attendancePercentage;
}
