package com.qrbats.qrbats.functionalities.attendance.dto.student;

import com.qrbats.qrbats.functionalities.attendance.dto.student.StudentAttendanceLectureDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentAttendanceResultDto {
    private List<StudentAttendanceLectureDto> studentAttendancesLectureList;
    private Integer attendanceCount;
    private Integer notAttendanceCount;
    private Integer finishedLectureCount;
    private Duration attendanceHours;
    private Duration notAttendanceHours;
    private Duration finishedLectureHours;
    private Double attendancePercentage;
}
