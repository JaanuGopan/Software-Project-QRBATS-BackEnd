package com.qrbats.qrbats.functionalities.locationfunc.dto;

import lombok.Data;

@Data
public class CreateLocationRequest {
    private Integer locationId;
    private String locationName;
    private double locationGPSLatitude;
    private double locationGPSLongitude;
    private double allowableRadius;
}
