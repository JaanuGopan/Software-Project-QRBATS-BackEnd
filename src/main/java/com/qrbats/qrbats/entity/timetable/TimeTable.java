package com.qrbats.qrbats.entity.timetable;

import com.qrbats.qrbats.authentication.entities.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;
import java.sql.Time;
import java.time.DayOfWeek;
@Data
@Entity
@Table(name = "time_table")
public class TimeTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer moduleId;
    private DayOfWeek dayOfWeek;
    private Time startTime;
    private Time endTime;
    private Integer locationId;
    private Date startDate;
    private Date endDate;
    private Integer createdBy;
}
