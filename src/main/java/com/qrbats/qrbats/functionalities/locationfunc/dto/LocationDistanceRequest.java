package com.qrbats.qrbats.functionalities.locationfunc.dto;

import lombok.Data;

@Data
public class LocationDistanceRequest {
    private String locationName;
    private double latitude;
    private double longitude;
}
