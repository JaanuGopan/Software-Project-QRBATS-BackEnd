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
    private String attendanceHours;
    private String notAttendanceHours;
    private String finishedLectureHours;
    private Double attendancePercentage;
}
