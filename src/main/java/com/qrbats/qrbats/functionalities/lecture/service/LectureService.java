package com.qrbats.qrbats.functionalities.lecture.service;

import com.qrbats.qrbats.config.AuditInfoProvider;
import com.qrbats.qrbats.entity.lecture.Lecture;
import com.qrbats.qrbats.entity.lecture.LectureRepository;
import com.qrbats.qrbats.entity.lecture.LectureStatus;
import com.qrbats.qrbats.entity.module.Module;
import com.qrbats.qrbats.entity.timetable.TimeTable;
import com.qrbats.qrbats.functionalities.module.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
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

    public List<Lecture> createLecturesForTimetable(TimeTable timeTable) {

        Module module = moduleService.getModuleByModuleId(timeTable.getModuleId());

        List<Date> allLecturesDates = new ArrayList<>();
        LocalDate start = timeTable.getStartDate().toLocalDate();
        LocalDate end = timeTable.getEndDate().toLocalDate();
        // Find the first occurrence of the specified day of the week
        while (!start.getDayOfWeek().equals(timeTable.getDayOfWeek())) {
            start = start.plusDays(1);
        }
        // Collect all dates matching the day of the week
        while (!start.isAfter(end)) {
            allLecturesDates.add(Date.valueOf(start));
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

            lectureList.add(lectureRepository.save(lecture));
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

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void changeLectureState() {
        Optional<List<Lecture>> pendingLectureList = lectureRepository.findAllByLectureStatus(LectureStatus.PENDING);
        pendingLectureList.ifPresent(lectures -> lectures.forEach(lecture -> {
            if (lecture.getLectureDate().equals(Date.valueOf(LocalDate.now()))
                    || lecture.getLectureDate().after(Date.valueOf(LocalDate.now()))) {
                if (lecture.getEndTime().after(Time.valueOf(LocalTime.now()))) {
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
            if (lecture.getLectureDate().equals(Date.valueOf(LocalDate.now()))
                    || lecture.getLectureDate().after(Date.valueOf(LocalDate.now()))) {
                if (lecture.getEndTime().after(Time.valueOf(LocalTime.now()))) {
                    lecture.setLectureStatus(LectureStatus.FINISHED);
                    lectureRepository.save(lecture);
                }
            }
        }));
    }
}
