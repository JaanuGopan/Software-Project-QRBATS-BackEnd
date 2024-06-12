package com.qrbats.qrbats.functionalities.export.dto;

import lombok.Data;

import java.sql.Date;
import java.sql.Time;

@Data
public class LectureReportResponseTable {
    private String studentName;
    private String studentIndexNo;
    private Date attendedDate;
    private Time attendedTime;
    private Boolean attendedStatus;
}
