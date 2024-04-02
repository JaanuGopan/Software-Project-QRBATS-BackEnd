package com.qrbats.qrbats.functionalities.locationfunc.service.impl;

import com.qrbats.qrbats.entity.location.Location;
import com.qrbats.qrbats.entity.location.LocationRepository;
import com.qrbats.qrbats.functionalities.locationfunc.dto.CreateLocationRequest;
import com.qrbats.qrbats.functionalities.locationfunc.service.LocationService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class LocationServiceImpl implements LocationService {
    private final LocationRepository repository;
    @Override
    public Location createLocation(CreateLocationRequest request) {
        Optional<Location> existLocation;
        Location location;
        if(request.getLocationId() != null){
            existLocation = repository.findById(request.getLocationId());
        }else {
            existLocation = Optional.empty();
        }
        location = existLocation.orElseGet(Location :: new);

        location.setLocationName(request.getLocationName());
        location.setLocationGPSLatitude(request.getLocationGPSLatitude());
        location.setLocationGPSLongitude(request.getLocationGPSLongitude());
        location.setAllowableRadius(request.getAllowableRadius());

        return repository.save(location);

    }
}
