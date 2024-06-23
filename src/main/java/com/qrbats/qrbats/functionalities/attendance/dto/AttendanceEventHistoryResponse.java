package com.qrbats.qrbats.functionalities.attendance.dto;

import lombok.Data;

import java.sql.Date;
import java.sql.Time;
@Data
public class AttendanceEventHistoryResponse {
    private String eventName;
    private String eventModuleCode;
    private String eventModuleName;
    private Time attendedTime;
    private Date attendedDate;
    private boolean attendanceStatus;
}
