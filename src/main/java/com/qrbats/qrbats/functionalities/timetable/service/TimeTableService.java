package com.qrbats.qrbats.functionalities.timetable.service;

import com.qrbats.qrbats.config.AuditInfoProvider;
import com.qrbats.qrbats.entity.lecture.Lecture;
import com.qrbats.qrbats.entity.lecture.LectureStatus;
import com.qrbats.qrbats.entity.lecture.LectureType;
import com.qrbats.qrbats.entity.module.Module;
import com.qrbats.qrbats.functionalities.lecture.dto.LectureDto;
import com.qrbats.qrbats.functionalities.lecture.service.LectureService;
import com.qrbats.qrbats.functionalities.module.service.ModuleService;
import com.qrbats.qrbats.functionalities.timetable.dto.TimeTableDto;
import com.qrbats.qrbats.functionalities.timetable.dto.schedule.DaySchedule;
import com.qrbats.qrbats.functionalities.timetable.dto.schedule.TimeSlot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeTableService {
    private final AuditInfoProvider auditInfoProvider;
    private final ModuleService moduleService;
    private final LectureService lectureService;

    public List<Lecture> saveTimeTable(TimeTableDto timeTableDto) {
        Module module = moduleService.getModuleByModuleId(timeTableDto.getModuleId());

        if (timeTableDto.getDayList().isEmpty()) {
            throw new IllegalArgumentException("Day list is empty");
        }
        if (timeTableDto.getDayScheduleList().isEmpty()) {
            throw new IllegalArgumentException("Day schedule list is empty");
        }
        if (timeTableDto.getLectureDatesForDayList().isEmpty()) {
            throw new IllegalArgumentException("Lecture date list is empty");
        }
        if (timeTableDto.getStartDate().isBefore(module.getStatingDate())) {
            throw new IllegalArgumentException("The timetable start date should be after module start date.");
        }
        if (timeTableDto.getEndDate().isAfter(module.getEndingDate())) {
            throw new IllegalArgumentException("The timetable end date should be before module end date.");
        }

        validateTimeTableDto(timeTableDto);

        List<Lecture> lectureList = new ArrayList<>();
        List<LectureDto> lectureDtoList = getLectureDtoListFromTimeTableDto(timeTableDto);
        // For validate the lecture.
        for (LectureDto lectureDto : lectureDtoList) {
            lectureService.validateSingleLectureDto(lectureDto);
        }
        // For save the lecture.
        for (LectureDto lectureDto : lectureDtoList) {
            Lecture lecture = lectureService.saveSingleLecture(lectureDto);
            lectureList.add(lecture);
        }
        return lectureList;
    }

    private List<LectureDto> getLectureDtoListFromTimeTableDto(TimeTableDto timeTableDto) {
        List<LectureDto> lectureDtoList = new ArrayList<>();
        for (DayOfWeek day : timeTableDto.getDayList()) {
            List<DaySchedule> dayScheduleList = timeTableDto.getDayScheduleList();
            List<TimeSlot> timeSlots = new ArrayList<>();
            for (DaySchedule daySchedule : dayScheduleList) {
                if (day == daySchedule.getDay()) {
                    timeSlots.addAll(daySchedule.getTimeSlots());
                }
            }

            for (LocalDate lectureDate : timeTableDto.getLectureDatesForDayList().get(day)) {
                for (TimeSlot timeSlot : timeSlots) {
                    LectureDto lectureDto = new LectureDto();
                    lectureDto.setLectureDate(lectureDate);
                    lectureDto.setLectureStartTime(timeSlot.getStartTime());
                    lectureDto.setLectureEndTime(timeSlot.getEndTime());
                    lectureDto.setLectureStatus(LectureStatus.PENDING);
                    lectureDto.setLectureType(LectureType.LECTURE);
                    lectureDto.setLocationId(timeSlot.getLocationId());
                    String lectureName = moduleService.getModuleByModuleId(timeTableDto.getModuleId()).getModuleCode()
                            + "_" + lectureDate + "_" + timeSlot.getStartTime();
                    lectureDto.setName(lectureName);
                    lectureDto.setModuleId(timeTableDto.getModuleId());
                    lectureDto.setCreatedBy(auditInfoProvider.getCurrentUserIdentifier());

                    lectureDtoList.add(lectureDto);
                }
            }
        }

        return lectureDtoList;
    }

    private void validateTimeTableDto(TimeTableDto timeTableDto) {
        if (timeTableDto.getStartDate() != null && timeTableDto.getStartDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("The timetable starting date should be in the future.");
        }
        if (timeTableDto.getEndDate() != null && timeTableDto.getEndDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("The timetable end date should be in the future.");
        }
        if (timeTableDto.getEndDate() != null && timeTableDto.getStartDate() != null &&
                timeTableDto.getEndDate().isBefore(timeTableDto.getStartDate())) {
            throw new IllegalArgumentException("The timetable end date should be after the starting date.");
        }

        for (DaySchedule daySchedule : timeTableDto.getDayScheduleList()) {
            for (TimeSlot timeSlot : daySchedule.getTimeSlots()) {
                LocalTime startTime = timeSlot.getStartTime();
                LocalTime endTime = timeSlot.getEndTime();
                if (endTime != null && startTime != null &&
                        endTime.isBefore(startTime)) {
                    throw new IllegalArgumentException("The timeslot end time should be after the start time for " + daySchedule.getDay().name() + ".");
                }
            }
        }
    }
}
