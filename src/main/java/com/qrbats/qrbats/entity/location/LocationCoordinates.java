package com.qrbats.qrbats.entity.location;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class LocationCoordinates {
    private Double latitude;
    private Double longitude;
}
