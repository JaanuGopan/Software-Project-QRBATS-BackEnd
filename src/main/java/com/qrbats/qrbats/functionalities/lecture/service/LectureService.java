package com.qrbats.qrbats.functionalities.lecture.service;

import com.qrbats.qrbats.config.AuditInfoProvider;
import com.qrbats.qrbats.entity.lecture.Lecture;
import com.qrbats.qrbats.entity.lecture.LectureRepository;
import com.qrbats.qrbats.entity.lecture.LectureStatus;
import com.qrbats.qrbats.entity.module.Module;
import com.qrbats.qrbats.entity.timetable.TimeTable;
import com.qrbats.qrbats.functionalities.lecture.dto.LectureDto;
import com.qrbats.qrbats.functionalities.module.service.ModuleService;
import com.qrbats.qrbats.service.scheduler.DynamicSchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LectureService {
    private final LectureRepository lectureRepository;
    private final ModuleService moduleService;
    private final AuditInfoProvider auditInfoProvider;
    private final DynamicSchedulerService dynamicSchedulerService;

    public List<Lecture> createLecturesForTimetable(TimeTable timeTable) {
        Module module = moduleService.getModuleByModuleId(timeTable.getModuleId());

        List<LocalDate> allLecturesDates = new ArrayList<>();
        LocalDate start = timeTable.getStartDate();
        LocalDate end = timeTable.getEndDate();
        // Find the first occurrence of the specified day of the week
        while (!start.getDayOfWeek().equals(timeTable.getDayOfWeek())) {
            start = start.plusDays(1);
        }
        // Collect all dates matching the day of the week
        while (!start.isAfter(end)) {
            allLecturesDates.add(start);
            start = start.plusWeeks(1);
        }

        List<Lecture> lectureList = new ArrayList<>();

        allLecturesDates.forEach(lectureDate -> {
            Lecture lecture = new Lecture();
            lecture.setLectureDate(lectureDate);
            lecture.setLectureStatus(LectureStatus.PENDING);
            lecture.setStartTime(timeTable.getStartTime());
            lecture.setEndTime(timeTable.getEndTime());
            lecture.setLocationId(timeTable.getLocationId());
            lecture.setModuleId(timeTable.getModuleId());
            lecture.setName(module.getModuleCode() + "_" + module.getStudentBatch() + "_" + timeTable.getDayOfWeek() + "_" + lectureDate.toString());
            lecture.setCreatedBy(auditInfoProvider.getCurrentUserIdentifier());

            Lecture savedLecture = lectureRepository.save(lecture);
            lectureList.add(savedLecture);

            // Automatically change the lecture stare to Finish after lecture complete.
            LocalDateTime executionTime = LocalDateTime.of(lectureDate, lecture.getEndTime());
            dynamicSchedulerService.scheduleLectureCompletion(savedLecture.getId(), () -> changeLectureStateByLectureId(savedLecture.getId()), executionTime);
        });

        return lectureList;
    }

    public Lecture findLectureById(Long id) {
        return lectureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found for this id - " + id));
    }

    public List<Lecture> getALlLectureByModuleId(Integer moduleId) {
        return lectureRepository.findAllByModuleId(moduleId)
                .orElseThrow(() -> new IllegalArgumentException("Lectures not found for this module."));
    }

    public Boolean checkLectureIsFinished(Lecture lecture) {
        if (lecture.getLectureDate().isEqual(LocalDate.now())
                || lecture.getLectureDate().isBefore(LocalDate.now())) {
            return lecture.getEndTime().isBefore(LocalTime.now());
        }
        return false;
    }

    public Duration calculateLectureDuration(Lecture lecture) {
        return Duration.between(lecture.getStartTime(), lecture.getEndTime());
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void changeLectureState() {
        Optional<List<Lecture>> pendingLectureList = lectureRepository.findAllByLectureStatus(LectureStatus.PENDING);
        pendingLectureList.ifPresent(lectures -> lectures.forEach(lecture -> {
            if (lecture.getLectureDate().equals(Date.valueOf(LocalDate.now()))
                    || lecture.getLectureDate().isAfter(LocalDate.now())) {
                if (lecture.getEndTime().isAfter(LocalTime.now())) {
                    lecture.setLectureStatus(LectureStatus.FINISHED);
                    lectureRepository.save(lecture);
                }
            }
        }));
    }

    public void changeLectureStateForModule(Integer moduleId) {
        Optional<List<Lecture>> pendingLectureList = lectureRepository
                .findAllByLectureStatusAndModuleId(LectureStatus.PENDING, moduleId);
        pendingLectureList.ifPresent(lectures -> lectures.forEach(lecture -> {
            if (lecture.getLectureDate().isEqual(LocalDate.now())
                    || lecture.getLectureDate().isAfter(LocalDate.now())) {
                if (lecture.getEndTime().isAfter(LocalTime.now())) {
                    lecture.setLectureStatus(LectureStatus.FINISHED);
                    lectureRepository.save(lecture);
                }
            }
        }));
    }

    public void changeLectureStateByLectureId(Long lectureId) {
        Lecture lecture = findLectureById(lectureId);
        if (lecture.getLectureDate().isAfter(LocalDate.now())
                || lecture.getLectureDate().equals(LocalDate.now())) {

            if (lecture.getEndTime().isAfter(LocalTime.now())) {
                lecture.setLectureStatus(LectureStatus.FINISHED);
                lectureRepository.save(lecture);
            }
        }
    }

    public List<Lecture> getAllLectureForUser() {
        return lectureRepository
                .findAllByCreatedBy(auditInfoProvider.getCurrentUserIdentifier()).orElse(new ArrayList<>());
    }
}
