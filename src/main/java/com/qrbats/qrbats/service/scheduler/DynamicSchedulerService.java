package com.qrbats.qrbats.service.scheduler;

import com.qrbats.qrbats.service.scheduler.entity.ScheduledJob;
import com.qrbats.qrbats.service.scheduler.entity.ScheduledJobRepository;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class DynamicSchedulerService {

    private final TaskScheduler taskScheduler;
    private final ScheduledJobRepository scheduledJobRepository;

    public DynamicSchedulerService(ScheduledJobRepository scheduledJobRepository) {
        this.scheduledJobRepository = scheduledJobRepository;
        this.taskScheduler = new ThreadPoolTaskScheduler();
    }

    public void scheduleLectureCompletion(Long lectureId, Runnable task, LocalDateTime executionTime) {
        ScheduledJob scheduledJob = new ScheduledJob(lectureId, executionTime);
        scheduledJobRepository.save(scheduledJob);

        taskScheduler.schedule(() -> {
            task.run();
            markJobAsCompleted(lectureId);
        }, executionTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private void markJobAsCompleted(Long lectureId) {
        ScheduledJob job = scheduledJobRepository.findById(lectureId).orElse(null);
        if (job != null) {
            job.setCompleted(true);
            scheduledJobRepository.save(job);
        }
    }
}

