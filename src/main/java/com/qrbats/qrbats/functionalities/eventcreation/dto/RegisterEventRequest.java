package com.qrbats.qrbats.functionalities.eventcreation.dto;

import com.qrbats.qrbats.entity.event.EventRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterEventRequest {
    private Integer eventId;
    private String eventName;
    private LocalDate eventDate;
    private LocalDate eventValidDate;
    private LocalTime eventTime;
    private LocalTime eventEndTime;
    private String eventVenue;
    private String eventModuleName;
    private String eventRole;
    private Integer eventAssignedUserId;
}
