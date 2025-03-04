package com.qrbats.qrbats.service.scheduler;

import com.qrbats.qrbats.functionalities.lecture.service.LectureService;
import com.qrbats.qrbats.service.scheduler.entity.ScheduledJob;
import com.qrbats.qrbats.service.scheduler.entity.ScheduledJobRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduledJobManager {
    @Autowired
    private ScheduledJobRepository scheduledJobRepository;
    @Autowired
    private DynamicSchedulerService dynamicSchedulerService;
    @Autowired
    private LectureService lectureService;

    @PostConstruct
    public void rescheduleMissedJobs() {
        List<ScheduledJob> missedJobs = scheduledJobRepository.findByCompletedFalseAndExecutionTimeBefore(LocalDateTime.now());

        for (ScheduledJob job : missedJobs) {
            dynamicSchedulerService.scheduleLectureCompletion(
                    job.getLectureId(),
                    () -> lectureService.changeLectureStateByLectureId(job.getLectureId()),
                    job.getExecutionTime()
            );
        }
    }
}

