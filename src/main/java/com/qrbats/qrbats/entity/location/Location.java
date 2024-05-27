package com.qrbats.qrbats.entity.location;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Location")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Location_id")
    private Integer locationId;

    @Column(name = "Location_name")
    private String locationName;

    @Column(name = "Location_gps_latitude")
    private double locationGPSLatitude;

    @Column(name = "Location_gps_longitude")
    private double locationGPSLongitude;

    @Column(name = "Location_allowable_radius")
    private double allowableRadius;
}
