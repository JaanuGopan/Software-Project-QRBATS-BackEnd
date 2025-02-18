package com.qrbats.qrbats.functionalities.attendance.lecture.dto;

import com.qrbats.qrbats.entity.location.LocationCoordinates;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LectureAttendanceMarkingDto {
    private Long lectureId;
    private LocationCoordinates locationCoordinates;
}
