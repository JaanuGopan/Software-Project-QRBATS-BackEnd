package com.qrbats.qrbats.functionalities.attendance.dto;

import com.qrbats.qrbats.entity.location.LocationCoordinates;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventAttendanceMarkingDto {
    private Long eventId;
    private LocationCoordinates locationCoordinates;
}
