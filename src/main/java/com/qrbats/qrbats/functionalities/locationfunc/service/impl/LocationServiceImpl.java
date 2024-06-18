package com.qrbats.qrbats.functionalities.locationfunc.service.impl;

import com.qrbats.qrbats.entity.location.Location;
import com.qrbats.qrbats.entity.location.LocationRepository;
import com.qrbats.qrbats.functionalities.locationfunc.dto.CreateLocationRequest;
import com.qrbats.qrbats.functionalities.locationfunc.dto.LocationDistanceRequest;
import com.qrbats.qrbats.functionalities.locationfunc.dto.LocationNameResponse;
import com.qrbats.qrbats.functionalities.locationfunc.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    @Override
    public void addLocations() {
        List<String> locations = Arrays.asList(
                "50,6.07953758447428,80.19206030748484,NCC",
                "50,6.079133810214307,80.19155422805409,LT1",
                "50,6.078789414282487,80.1912795418734,LT2",
                "50,6.079015053021337,80.19112428446691,Auditorium",
                //"50,6.079493081707097,80.19168340470081,Library",
                //"50,6.079493081707097,80.19168340470081,Study Hall",
                "50,6.078150890028423,80.19217574681655,DEIE",
                "50,6.078473748481698,80.19184438229699,DMME",
                "50,6.0781203313993455,80.19141948746949,DCEE",
                "50,6.077142619941645,80.19093951556941,Mechanical Workshop area",
                //"50,6.0787961303052525,80.19185914403491,Nuga Tree",
                "50,7.573874549824014, 81.7847930867964,MyHome"
        );

        for (String location : locations) {
            String[] values = location.split(",");
            Location entity = new Location();
            entity.setAllowableRadius(Double.parseDouble(values[0]));
            entity.setLocationGPSLatitude(Double.parseDouble(values[1]));
            entity.setLocationGPSLongitude(Double.parseDouble(values[2]));
            entity.setLocationName(values[3]);
            repository.save(entity);
        }
    }

    @Override
    public List<LocationNameResponse> getAllLocationName() {
        List<Location> locationList = repository.findAll();
        List<LocationNameResponse> returnList = new ArrayList<>();
        for (Location location : locationList){
            LocationNameResponse locationNameResponse = new LocationNameResponse();
            locationNameResponse.setLocationName(location.getLocationName());
            returnList.add(locationNameResponse);
        }
        return returnList;
    }


}
