package com.qrbats.qrbats.functionalities.lecture.service;

import com.qrbats.qrbats.config.AuditInfoProvider;
import com.qrbats.qrbats.entity.lecture.Lecture;
import com.qrbats.qrbats.entity.lecture.LectureRepository;
import com.qrbats.qrbats.entity.lecture.LectureStatus;
import com.qrbats.qrbats.entity.location.Location;
import com.qrbats.qrbats.entity.module.Module;
import com.qrbats.qrbats.functionalities.lecture.dto.LectureViewDto;
import com.qrbats.qrbats.functionalities.location.service.LocationService;
import com.qrbats.qrbats.functionalities.module.service.ModuleService;
import com.qrbats.qrbats.functionalities.timetable.dto.TimeTableDto;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.Duration;
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
    private final LocationService locationService;

    public List<Lecture> createLecturesForTimetable(TimeTableDto timeTableDto) {
        Module module = moduleService.getModuleByModuleId(timeTableDto.getModuleId());

        List<LocalDate> allLecturesDates = new ArrayList<>(timeTableDto.getLectureDateList());
        for (LocalDate allLecturesDate : allLecturesDates) {
            validateLectureDto(allLecturesDate,timeTableDto);
        }

        List<Lecture> lectureList = new ArrayList<>();

        allLecturesDates.forEach(lectureDate -> {
            Lecture lecture = new Lecture();
            lecture.setLectureDate(lectureDate);
            lecture.setLectureStatus(LectureStatus.PENDING);
            lecture.setStartTime(timeTableDto.getStartTime());
            lecture.setEndTime(timeTableDto.getEndTime());
            lecture.setLocationId(timeTableDto.getLocationId());
            lecture.setModuleId(timeTableDto.getModuleId());
            lecture.setName(module.getModuleCode() + "_" + module.getStudentBatch() + "_" + timeTableDto.getDayOfWeek() + "_" + lectureDate.toString());
            lecture.setCreatedBy(auditInfoProvider.getCurrentUserIdentifier());

            Lecture savedLecture = lectureRepository.save(lecture);
            lectureList.add(savedLecture);
        });

        return lectureList;
    }

    public void validateLectureDto(LocalDate lectureDate, TimeTableDto timeTableDto) {
        LocalTime startTime = timeTableDto.getStartTime();
        LocalTime endTime = timeTableDto.getEndTime();
        Integer locationId = timeTableDto.getLocationId();
        Module module = moduleService.getModuleByModuleId(timeTableDto.getModuleId());
        Location location = locationService.getLocationById(locationId);

        if (startTime.isAfter(endTime)){
            throw new IllegalArgumentException("The lecture start time should be before lecture end time.");
        }
        if (lectureDate.isBefore(LocalDate.now())){
            throw new IllegalArgumentException("The lecture date should not be in past for " + lectureDate + " lecture.");
        }
        if (lectureDate.isEqual(LocalDate.now())){
            if (startTime.isBefore(LocalTime.now())){
                throw new IllegalArgumentException("The lecture start time should be in future for " + lectureDate + " lecture.");
            }
        }

        Optional<Lecture> lecture = getLectureByDateAndLocationIdBetweenStartTimeAndEndTime(
                lectureDate, locationId, startTime, endTime
        );
        if (lecture.isPresent()) {
            throw new IllegalArgumentException("There is another lecture on " + lectureDate.toString()
                    + " at " + lecture.get().getStartTime() + " in " + location.getLocationName() +"."
            );
        }

    }

    public Optional<Lecture> getLectureByDateAndLocationIdBetweenStartTimeAndEndTime(
            LocalDate lectureDate,
            Integer locationId,
            LocalTime startTime,
            LocalTime endTime
    ) {
        return lectureRepository
                .findByLectureDateAndLocationIdAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                        lectureDate, locationId, startTime, endTime
                );
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
        if (!lecture.getLectureDate().isAfter(LocalDate.now())) {
            if (lecture.getLectureDate().isEqual(LocalDate.now())) {
                return lecture.getEndTime().isBefore(LocalTime.now());
            }
            return true;
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

    public List<LectureViewDto> getAllLectureForUserByModuleId(Integer moduleId) {
        List<Lecture> lectureList = lectureRepository
                .findAllByCreatedByAndModuleId(auditInfoProvider.getCurrentUserIdentifier(), moduleId).orElse(new ArrayList<>());
        List<LectureViewDto> lectureViewDtoList = new ArrayList<>();
        for (Lecture lecture : lectureList) {
            LectureViewDto lectureViewDto = new LectureViewDto();
            lectureViewDto.setId(lecture.getId());
            lectureViewDto.setLectureEndTime(lecture.getEndTime());
            lectureViewDto.setLectureStartTime(lecture.getStartTime());
            lectureViewDto.setLectureStatus(lecture.getLectureStatus());
            lectureViewDto.setLectureDate(lecture.getLectureDate());
            lectureViewDto.setLocationName(locationService.getLocationName(lecture.getLocationId()));
            lectureViewDto.setModuleCode(moduleService.getModuleByModuleId(lecture.getModuleId()).getModuleCode());
            lectureViewDto.setModuleId(moduleId);
            lectureViewDto.setName(lecture.getName());
            lectureViewDto.setCreatedBy(lecture.getCreatedBy());

            lectureViewDtoList.add(lectureViewDto);
        }
        return lectureViewDtoList;
    }

    public List<LectureViewDto> getAllLectureForStudentByModuleId(Integer moduleId) {
        Boolean isStudentEnrolledTheModule = moduleService.checkStudentModuleEnrollment(moduleId);
        List<Lecture> lectureList = lectureRepository
                .findAllByModuleId(moduleId).orElse(new ArrayList<>());
        List<LectureViewDto> lectureViewDtoList = new ArrayList<>();
        for (Lecture lecture : lectureList) {
            if (!lecture.getLectureDate().isBefore(LocalDate.now())) {
                if (!(lecture.getLectureDate().isEqual(LocalDate.now()) && lecture.getEndTime().isBefore(LocalTime.now()))) {
                    LectureViewDto lectureViewDto = new LectureViewDto();
                    lectureViewDto.setId(lecture.getId());
                    lectureViewDto.setLectureEndTime(lecture.getEndTime());
                    lectureViewDto.setLectureStartTime(lecture.getStartTime());
                    lectureViewDto.setLectureStatus(lecture.getLectureStatus());
                    lectureViewDto.setLectureDate(lecture.getLectureDate());
                    lectureViewDto.setLocationName(locationService.getLocationName(lecture.getLocationId()));
                    lectureViewDto.setModuleCode(moduleService.getModuleByModuleId(lecture.getModuleId()).getModuleCode());
                    lectureViewDto.setName(lecture.getName());
                    lectureViewDto.setCreatedBy(lecture.getCreatedBy());
                    lectureViewDto.setModuleId(moduleId);

                    lectureViewDtoList.add(lectureViewDto);
                }
            }
        }
        return lectureViewDtoList;
    }

    public List<LectureViewDto> getAllLectureForUser() {
        List<Lecture> lectureList = lectureRepository
                .findAllByCreatedBy(auditInfoProvider.getCurrentUserIdentifier()).orElse(new ArrayList<>());
        List<LectureViewDto> lectureViewDtoList = new ArrayList<>();
        for (Lecture lecture : lectureList) {
            LectureViewDto lectureViewDto = new LectureViewDto();
            lectureViewDto.setId(lecture.getId());
            lectureViewDto.setLectureEndTime(lecture.getEndTime());
            lectureViewDto.setLectureStartTime(lecture.getStartTime());
            lectureViewDto.setLectureStatus(lecture.getLectureStatus());
            lectureViewDto.setLectureDate(lecture.getLectureDate());
            lectureViewDto.setLocationName(locationService.getLocationName(lecture.getLocationId()));
            lectureViewDto.setModuleCode(moduleService.getModuleByModuleId(lecture.getModuleId()).getModuleCode());
            lectureViewDto.setName(lecture.getName());
            lectureViewDto.setCreatedBy(lecture.getCreatedBy());
            lectureViewDto.setModuleId(lecture.getModuleId());

            lectureViewDtoList.add(lectureViewDto);
        }
        return lectureViewDtoList;
    }
}
