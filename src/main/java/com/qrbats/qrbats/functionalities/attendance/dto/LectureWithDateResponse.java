package com.qrbats.qrbats.functionalities.attendance.dto;

import lombok.Data;

import java.sql.Date;
import java.sql.Time;

@Data
public class LectureWithDateResponse {
    private Integer lectureId;
    private String lectureName;
    private Date lectureDate;
    private Time lectureStartTime;
    private Time lectureEndTime;
    private String lectureModuleCode;
    private String lectureVenue;
}
