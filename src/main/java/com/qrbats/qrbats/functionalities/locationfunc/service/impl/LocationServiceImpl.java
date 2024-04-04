package com.qrbats.qrbats.functionalities.locationfunc.service.impl;

import com.qrbats.qrbats.entity.location.Location;
import com.qrbats.qrbats.entity.location.LocationRepository;
import com.qrbats.qrbats.functionalities.locationfunc.dto.CreateLocationRequest;
import com.qrbats.qrbats.functionalities.locationfunc.dto.LocationDistanceRequest;
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


    public double distanceCalculator(double lat1, double lon1, double lat2, double lon2) {
        // Radius of the Earth in kilometers
        final double R = 6371.0;

        // Convert latitude and longitude from degrees to radians
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Calculate the distance
        double distance = R * c * 1000;
        System.out.println("The distance is : "+ distance);
        return distance;
    }

    @Override
    public double getDistance(LocationDistanceRequest request) {
        Optional<Location> location = repository.findByLocationName(request.getLocationName());
        if (location.isPresent()){
            return distanceCalculator(request.getLatitude(),request.getLongitude(),location.get().getLocationGPSLatitude(),location.get().getLocationGPSLongitude());
        }else {
            throw new RuntimeException("Location not found.");
        }
    }


}
