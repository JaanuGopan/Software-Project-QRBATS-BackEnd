package com.qrbats.qrbats.entity.lecture;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private LocalDate lectureDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer locationId;
    private LectureStatus lectureStatus;
    private Integer moduleId;
    private Integer createdBy;
    private LectureType lectureType;
}
