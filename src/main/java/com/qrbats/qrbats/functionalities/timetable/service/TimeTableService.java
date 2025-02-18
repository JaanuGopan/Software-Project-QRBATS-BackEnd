package com.qrbats.qrbats.functionalities.timetable.service;

import com.qrbats.qrbats.config.AuditInfoProvider;
import com.qrbats.qrbats.entity.timetable.TimeTable;
import com.qrbats.qrbats.entity.timetable.TimeTableRepository;
import com.qrbats.qrbats.functionalities.timetable.dto.TimeTableDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeTableService {
    private final TimeTableRepository timeTableRepository;
    private final AuditInfoProvider auditInfoProvider;

    public TimeTable saveTimeTable(TimeTableDto timeTableDto) {
        validateTimeTableDto(timeTableDto);

        TimeTable timeTable;
        if (timeTableDto.getId() == null) {
            timeTable = new TimeTable();
            mapDtoToEntity(timeTableDto, timeTable);
            timeTable.setCreatedBy(auditInfoProvider.getCurrentUserIdentifier());
        } else {
            timeTable = timeTableRepository.findById(timeTableDto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("No timetable found with ID: " + timeTableDto.getId()));
            validateAuthorization(timeTable);
            mapDtoToEntity(timeTableDto, timeTable);
        }
        return timeTableRepository.save(timeTable);
    }

    public void deleteTimeTable(Integer id) {
        TimeTable timeTable = timeTableRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No timetable found with ID: " + id));
        validateAuthorization(timeTable);
        timeTableRepository.deleteById(id);
    }

    private void validateTimeTableDto(TimeTableDto timeTableDto) {
        if (timeTableDto.getStartingDate() != null && timeTableDto.getStartingDate().before(Date.valueOf(LocalDate.now()))) {
            throw new IllegalArgumentException("The timetable starting date should be in the future.");
        }
        if (timeTableDto.getValidDate() != null && timeTableDto.getValidDate().before(Date.valueOf(LocalDate.now()))) {
            throw new IllegalArgumentException("The timetable valid date should be in the future.");
        }
        if (timeTableDto.getValidDate() != null && timeTableDto.getStartingDate() != null &&
                timeTableDto.getValidDate().before(timeTableDto.getStartingDate())) {
            throw new IllegalArgumentException("The timetable valid date should be after the starting date.");
        }
        if (timeTableDto.getEndTime() != null && timeTableDto.getStartTime() != null &&
                timeTableDto.getEndTime().before(timeTableDto.getStartTime())) {
            throw new IllegalArgumentException("The timetable end time should be after the start time.");
        }

        List<TimeTable> conflictingTimeTables = timeTableRepository.findConflictingTimeTables(
          timeTableDto.getDayOfWeek(),
          timeTableDto.getLocationId(),
          timeTableDto.getStartTime(),
          timeTableDto.getEndTime()
        );

        if (!conflictingTimeTables.isEmpty()) {
            throw new IllegalArgumentException("Already there is a timetable in same location and day, it leads time conflict.");
        }
    }

    private void validateAuthorization(TimeTable timeTable) {
        if (!timeTable.getCreatedBy().equals(auditInfoProvider.getCurrentUserIdentifier())) {
            throw new IllegalArgumentException("You are not authorized to perform this action.");
        }
    }

    private void mapDtoToEntity(TimeTableDto dto, TimeTable entity) {
        if (dto.getModuleId() != null) entity.setModuleId(dto.getModuleId());
        if (dto.getStartTime() != null) entity.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null) entity.setEndTime(dto.getEndTime());
        if (dto.getDayOfWeek() != null) entity.setDayOfWeek(dto.getDayOfWeek());
        if (dto.getStartingDate() != null) entity.setStartDate(dto.getStartingDate());
        if (dto.getValidDate() != null) entity.setEndDate(dto.getValidDate());
    }
}
