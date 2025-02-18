package com.qrbats.qrbats.entity.lecture;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;
@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lecture {
    @Id
    private Long id;
    private String name;
    private Date lectureDate;
    private Time startTime;
    private Time endTime;
    private Integer locationId;
    private LectureStatus lectureStatus;
    private Integer moduleId;
    private Integer createdBy;
}
