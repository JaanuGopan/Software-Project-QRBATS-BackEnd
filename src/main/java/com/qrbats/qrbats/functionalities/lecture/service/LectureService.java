package com.qrbats.qrbats.functionalities.lecture.service;

import com.qrbats.qrbats.config.AuditInfoProvider;
import com.qrbats.qrbats.entity.lecture.Lecture;
import com.qrbats.qrbats.entity.lecture.LectureRepository;
import com.qrbats.qrbats.entity.lecture.LectureStatus;
import com.qrbats.qrbats.entity.lecture.LectureType;
import com.qrbats.qrbats.entity.location.Location;
import com.qrbats.qrbats.entity.module.Module;
import com.qrbats.qrbats.functionalities.lecture.dto.LectureConflictDto;
import com.qrbats.qrbats.functionalities.lecture.dto.LectureDto;
import com.qrbats.qrbats.functionalities.lecture.dto.LectureViewDto;
import com.qrbats.qrbats.functionalities.location.service.LocationService;
import com.qrbats.qrbats.functionalities.module.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LectureService {
    private final LectureRepository lectureRepository;
    private final ModuleService moduleService;
    private final AuditInfoProvider auditInfoProvider;
    private final LocationService locationService;

    @Value("${lecture.checkLectureConflict}")
    private boolean checkLectureConflict;
    public Lecture saveSingleLecture(LectureDto lectureDto) {
        validateSingleLectureDto(lectureDto);

        Lecture lecture;
        if (lectureDto.getId() == null) {
            lecture = new Lecture();

            lecture.setName(lectureDto.getName());
            lecture.setCreatedBy(auditInfoProvider.getCurrentUserIdentifier());
            lecture.setLectureDate(lectureDto.getLectureDate());
            lecture.setLectureStatus(LectureStatus.PENDING);
            lecture.setLectureType(lectureDto.getLectureType());
            lecture.setEndTime(lectureDto.getLectureEndTime());
            lecture.setStartTime(lectureDto.getLectureStartTime());
            lecture.setLocationId(lectureDto.getLocationId());
            if (lectureDto.getModuleId() != null) lecture.setModuleId(lectureDto.getModuleId());
        } else {
            lecture = findLectureById(lectureDto.getId());
            if (!lecture.getCreatedBy().equals(auditInfoProvider.getCurrentUserIdentifier())) {
                throw new IllegalArgumentException("You are not authorized.");
            }
            if (checkLectureIsFinished(lecture)) {
                //throw new IllegalArgumentException("Lecture is already finished.");
            }

            if (lectureDto.getName() != null) lecture.setName(lectureDto.getName());
            if (lectureDto.getLectureDate() != null) lecture.setLectureDate(lectureDto.getLectureDate());
            if (lectureDto.getLectureStatus() != null) lecture.setLectureStatus(lectureDto.getLectureStatus());
            if (lectureDto.getLectureType() != null) lecture.setLectureType(lectureDto.getLectureType());
            if (lectureDto.getLectureEndTime() != null) lecture.setEndTime(lectureDto.getLectureEndTime());
            if (lectureDto.getLectureStartTime() != null) lecture.setStartTime(lectureDto.getLectureStartTime());
            if (lectureDto.getLocationId() != null) lecture.setLocationId(lectureDto.getLocationId());
            if (lectureDto.getModuleId() != null) lecture.setModuleId(lectureDto.getModuleId());
        }

        return lectureRepository.save(lecture);
    }

    public void deleteLectureById(Long lectureId) {
        Lecture lecture = findLectureById(lectureId);
        if (checkLectureIsFinished(lecture)) {
            throw new IllegalArgumentException("Lecture is already finished.");
        }
        lectureRepository.delete(lecture);
    }

    public void deleteLecturesByModuleId(Integer moduleId) {
        Module module = moduleService.getModuleByModuleId(moduleId);
        lectureRepository.deleteAllByModuleId(moduleId);
    }

    public void validateSingleLectureDto(LectureDto lectureDto) {
        if (lectureDto.getModuleId() != null) {
            Module module = moduleService.getModuleByModuleId(lectureDto.getModuleId());
        }

        validateLecture(lectureDto);
    }

    private void validateLecture(LectureDto lectureDto) {

        LocalDate lectureDate = lectureDto.getLectureDate();
        LocalTime startTime = lectureDto.getLectureStartTime();
        LocalTime endTime = lectureDto.getLectureEndTime();
        Integer locationId = lectureDto.getLocationId();
        Location location = locationService.getLocationById(locationId);

        if (checkLectureConflict) {
            List<Lecture> conflictLectures = getLectureByDateAndLocationIdBetweenStartTimeAndEndTime(
                    lectureDate, locationId, startTime, endTime
            );

            if (!conflictLectures.isEmpty()) {
                if (lectureDto.getId() != null) {
                    if (conflictLectures.size() > 1) {
                        throw new IllegalArgumentException("Multiple scheduling conflicts detected on " + lectureDate
                                + " at " + location.getLocationName() + ". Conflicting lectures: " +
                                conflictLectures.stream()
                                        .map(l -> "[Start: " + l.getStartTime() + ", End: " + l.getEndTime() + "]")
                                        .collect(Collectors.joining(", ")) + ". Please choose a different time.");
                    }
                    if (!conflictLectures.get(0).getId().equals(lectureDto.getId())) {
                        throw new IllegalArgumentException("A scheduling conflict exists on " + lectureDate
                                + " at " + location.getLocationName() + ". Conflicting lecture: [Start: " +
                                conflictLectures.get(0).getStartTime() + ", End: " + conflictLectures.get(0).getEndTime() + "]. Please adjust the timing.");
                    }
                } else {
                    throw new IllegalArgumentException("A scheduling conflict exists on " + lectureDate
                            + " at " + location.getLocationName() + ". Conflicting lectures: " +
                            conflictLectures.stream()
                                    .map(l -> "[Start: " + l.getStartTime() + ", End: " + l.getEndTime() + "]")
                                    .collect(Collectors.joining(", ")) + ". Please select another time.");
                }
            }
        }

        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("The lecture start time should be before lecture end time.");
        }

        if (lectureDto.getId() == null) {
            if (lectureDate.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("The lecture date should not be in past for " + lectureDate + " lecture.");
            }
            if (lectureDate.isEqual(LocalDate.now())) {
                if (startTime.isBefore(LocalTime.now())) {
                    throw new IllegalArgumentException("The lecture start time should be in future for " + lectureDate + " lecture.");
                }
            }
        } else {
            // handle lecture edit validation.
        }
    }

    public List<Lecture> getLectureByDateAndLocationIdBetweenStartTimeAndEndTime(
            LocalDate lectureDate,
            Integer locationId,
            LocalTime startTime,
            LocalTime endTime
    ) {
        Optional<List<Lecture>> lectureConflicts = lectureRepository
                .findAllLectureConflicts(lectureDate, locationId, startTime, endTime);

        List<Lecture> conflicts = new ArrayList<>();

        if (lectureConflicts.isPresent()) {
            for (Lecture lecture : lectureConflicts.get()) {
                if (lecture.getLectureType().equals(LectureType.LECTURE)) {
                    if (moduleService.checkModule(lecture.getModuleId())) {
                        conflicts.add(lecture);
                    }
                } else if (lecture.getLectureType().equals(LectureType.EVENT)) {
                    conflicts.add(lecture);
                }
            }
        }
        return conflicts;
    }

    public boolean checkLectureTimeConflict(LectureConflictDto lectureConflictDto) {
        return !getLectureByDateAndLocationIdBetweenStartTimeAndEndTime(
                lectureConflictDto.getLectureDate(),
                lectureConflictDto.getLocationId(),
                lectureConflictDto.getStartTime(),
                lectureConflictDto.getEndTime()
        ).isEmpty();
    }

    public Lecture findLectureById(Long id) {
        Lecture lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found for this id - " + id));
        if (lecture.getLectureType().equals(LectureType.LECTURE) && moduleService.checkModule(lecture.getModuleId())) {
            return lecture;
        } else if (lecture.getLectureType().equals(LectureType.EVENT)) {
            return lecture;
        }
        throw new IllegalArgumentException("Lecture not found for this id - " + id);
    }

    public LectureViewDto getLectureById(Long id) {
        Lecture lecture = findLectureById(id);

        LectureViewDto lectureViewDto = new LectureViewDto();
        lectureViewDto.setId(lecture.getId());
        lectureViewDto.setName(lecture.getName());
        lectureViewDto.setLectureEndTime(lecture.getEndTime());
        lectureViewDto.setLectureStartTime(lecture.getStartTime());
        lectureViewDto.setLectureStatus(lecture.getLectureStatus());
        lectureViewDto.setLectureDate(lecture.getLectureDate());
        lectureViewDto.setLocationName(locationService.getLocationName(lecture.getLocationId()));
        lectureViewDto.setCreatedBy(lecture.getCreatedBy());
        lectureViewDto.setLectureType(lecture.getLectureType());

        return lectureViewDto;
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

    public List<LectureViewDto> getAllLectureForUserByModuleId(Integer moduleId) {
        List<Lecture> lectureList = lectureRepository
                .findAllByCreatedByAndModuleId(auditInfoProvider.getCurrentUserIdentifier(), moduleId).orElse(new ArrayList<>());
        List<LectureViewDto> lectureViewDtoList = new ArrayList<>();
        for (Lecture lecture : lectureList) {
            if (moduleService.checkModule(lecture.getModuleId())) {
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
                lectureViewDto.setLectureType(lecture.getLectureType());

                lectureViewDtoList.add(lectureViewDto);
            }
        }
        return lectureViewDtoList;
    }

    public List<LectureViewDto> getAllFinishedLectureForUserByModuleId(Integer moduleId) {
        List<Lecture> lectureList = lectureRepository
                .findAllByCreatedByAndModuleId(auditInfoProvider.getCurrentUserIdentifier(), moduleId).orElse(new ArrayList<>());
        lectureList.sort(Comparator.comparing(Lecture::getLectureDate));
        List<LectureViewDto> lectureViewDtoList = new ArrayList<>();
        for (Lecture lecture : lectureList) {
            if (moduleService.checkModule(lecture.getModuleId())) {
                if (checkLectureIsFinished(lecture) || (lecture.getLectureDate().isEqual(LocalDate.now()) && lecture.getStartTime().isBefore(LocalTime.now()))) {
                    LectureViewDto lectureViewDto = new LectureViewDto();
                    lectureViewDto.setId(lecture.getId());
                    lectureViewDto.setLectureEndTime(lecture.getEndTime());
                    lectureViewDto.setLectureStartTime(lecture.getStartTime());
                    lectureViewDto.setLectureStatus(LectureStatus.FINISHED);
                    lectureViewDto.setLectureDate(lecture.getLectureDate());
                    lectureViewDto.setLocationName(locationService.getLocationName(lecture.getLocationId()));
                    lectureViewDto.setModuleCode(moduleService.getModuleByModuleId(lecture.getModuleId()).getModuleCode());
                    lectureViewDto.setModuleId(moduleId);
                    lectureViewDto.setName(lecture.getName());
                    lectureViewDto.setCreatedBy(lecture.getCreatedBy());
                    lectureViewDto.setLectureType(lecture.getLectureType());

                    lectureViewDtoList.add(lectureViewDto);
                }
            }
        }
        return lectureViewDtoList;
    }

    public List<LectureViewDto> getAllLectureForStudentByModuleId(Integer moduleId) {
        Boolean isStudentEnrolledTheModule = moduleService.checkStudentModuleEnrollment(moduleId);
        List<Lecture> lectureList = lectureRepository
                .findAllByModuleId(moduleId).orElse(new ArrayList<>());
        List<LectureViewDto> lectureViewDtoList = new ArrayList<>();
        for (Lecture lecture : lectureList) {
            if (moduleService.checkModule(lecture.getModuleId())) {
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
                        lectureViewDto.setLectureType(lecture.getLectureType());

                        lectureViewDtoList.add(lectureViewDto);
                    }
                }
            }
        }
        return lectureViewDtoList;
    }

    public List<LectureViewDto> getAllLectureForUser() {
        List<Lecture> lectureList = lectureRepository
                .findAllByCreatedByAndLectureType(auditInfoProvider.getCurrentUserIdentifier(), LectureType.LECTURE).orElse(new ArrayList<>());
        List<LectureViewDto> lectureViewDtoList = new ArrayList<>();
        for (Lecture lecture : lectureList) {
            if (moduleService.checkModule(lecture.getModuleId())) {
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
                lectureViewDto.setLectureType(lecture.getLectureType());

                lectureViewDtoList.add(lectureViewDto);
            }
        }
        return lectureViewDtoList;
    }

    public List<LectureViewDto> getAllEventsForUser() {
        List<Lecture> lectureList = lectureRepository
                .findAllByCreatedByAndLectureType(auditInfoProvider.getCurrentUserIdentifier(), LectureType.EVENT).orElse(new ArrayList<>());
        List<LectureViewDto> lectureViewDtoList = new ArrayList<>();
        for (Lecture lecture : lectureList) {
            LectureViewDto lectureViewDto = new LectureViewDto();
            lectureViewDto.setId(lecture.getId());
            lectureViewDto.setLectureEndTime(lecture.getEndTime());
            lectureViewDto.setLectureStartTime(lecture.getStartTime());
            lectureViewDto.setLectureStatus(lecture.getLectureStatus());
            lectureViewDto.setLectureDate(lecture.getLectureDate());
            lectureViewDto.setLocationName(locationService.getLocationName(lecture.getLocationId()));
            lectureViewDto.setName(lecture.getName());
            lectureViewDto.setCreatedBy(lecture.getCreatedBy());
            lectureViewDto.setLectureType(lecture.getLectureType());

            lectureViewDtoList.add(lectureViewDto);
        }
        return lectureViewDtoList;
    }
}
