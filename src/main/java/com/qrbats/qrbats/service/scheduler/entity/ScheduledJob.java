package com.qrbats.qrbats.service.scheduler.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduledJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long lectureId;
    private LocalDateTime executionTime;
    private boolean completed = false;

    public ScheduledJob(Long lectureId, LocalDateTime executionTime) {
        this.lectureId = lectureId;
        this.executionTime = executionTime;
    }
}
