package com.qrbats.qrbats.service.scheduler.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduledJobRepository extends JpaRepository<ScheduledJob, Long> {
    List<ScheduledJob> findByCompletedFalseAndExecutionTimeBefore(LocalDateTime now);
}
