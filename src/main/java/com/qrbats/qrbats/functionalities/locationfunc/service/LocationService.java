package com.qrbats.qrbats.functionalities.locationfunc.service;

import com.qrbats.qrbats.entity.location.Location;
import com.qrbats.qrbats.functionalities.locationfunc.dto.LocationDistanceRequest;
import com.qrbats.qrbats.functionalities.locationfunc.dto.CreateLocationRequest;

public interface LocationService {
    Location createLocation(CreateLocationRequest request);
    double getDistance(LocationDistanceRequest request);

}
