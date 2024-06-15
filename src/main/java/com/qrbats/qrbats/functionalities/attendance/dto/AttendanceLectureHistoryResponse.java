package com.qrbats.qrbats.functionalities.attendance.dto;

import lombok.Data;

import java.sql.Date;
import java.sql.Time;

@Data
public class AttendanceLectureHistoryResponse {
    private String lectureName;
    private String lectureModuleCode;
    private String lectureModuleName;
    private Time attendedTime;
    private Date attendedDate;
    private boolean attendanceStatus;

}
