package com.qrbats.qrbats.entity.lecture;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Lecture")
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Lecture_id")
    private Integer lectureId;

    @Column(name = "Lecture_name")
    private String lectureName;

    @Column(name = "Lecture_day")
    private String lectureDay;

    @Column(name = "Lecture_venue")
    private String lectureVenue;

    @Column(name = "Lecture_start_time")
    private Time lectureStartTime;

    @Column(name = "Lecture_end_time")
    private Time lectureEndTime;

    @Column(name = "Lecture_assigned_user_id")
    private Integer lectureAssignedUserId;

    @Column(name = "Lecture_module_code")
    private String lectureModuleCode;
}
