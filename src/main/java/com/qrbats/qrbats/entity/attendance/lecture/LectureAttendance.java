package com.qrbats.qrbats.entity.attendance.lecture;

import com.qrbats.qrbats.entity.attendance.AttendanceStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table
public class LectureAttendance {
    @Id
    private Long id;
    private Integer attendeeId;
    private Long lectureId;
    private Integer moduleId;
    private Date attendedDate;
    private Time attendedTime;
    private AttendanceStatus attendanceStatus;
}
