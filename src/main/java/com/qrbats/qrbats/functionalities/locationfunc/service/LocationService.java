package com.qrbats.qrbats.functionalities.locationfunc.service;

import com.qrbats.qrbats.entity.location.Location;
import com.qrbats.qrbats.functionalities.locationfunc.dto.LocationDistanceRequest;
import com.qrbats.qrbats.functionalities.locationfunc.dto.CreateLocationRequest;
import com.qrbats.qrbats.functionalities.locationfunc.dto.LocationNameResponse;

import java.util.List;

public interface LocationService {
    Location createLocation(CreateLocationRequest request);
    double getDistance(LocationDistanceRequest request);
    void addLocations();

    List<LocationNameResponse> getAllLocationName();
}
