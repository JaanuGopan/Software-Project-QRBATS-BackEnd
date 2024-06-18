package com.qrbats.qrbats.functionalities.attendance.dto;

import lombok.Data;

import java.sql.Date;
import java.sql.Time;

@Data
public class LectureAttendanceMarkingByLectureIdRequest {
    private Integer studentId;
    private Integer lectureId;
    private double longitude;
    private double latitude;
    private Time attendedTime;
    private Date attendedDate;
}
