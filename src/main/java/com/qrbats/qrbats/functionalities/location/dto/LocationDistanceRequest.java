package com.qrbats.qrbats.functionalities.location.dto;

import lombok.Data;

@Data
public class LocationDistanceRequest {
    private String locationName;
    private double latitude;
    private double longitude;
}
