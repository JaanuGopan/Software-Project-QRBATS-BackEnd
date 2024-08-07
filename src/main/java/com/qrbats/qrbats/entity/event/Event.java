package com.qrbats.qrbats.entity.event;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Event")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Event_id")
    private Integer eventId;

    @Column(name = "Event_name")
    private String eventName;

    @Column(name = "Event_date")
    private LocalDate eventDate;

    @Column(name = "Event_venue")
    private String eventVenue;

    @Column(name = "Event_time")
    private LocalTime eventTime;

    @Column(name = "Event_end_time")
    private LocalTime eventEndTime;

    @Column(name = "Event_assigned_user_id")
    private Integer eventAssignedUserId;

    private EventRole eventRole;

}
