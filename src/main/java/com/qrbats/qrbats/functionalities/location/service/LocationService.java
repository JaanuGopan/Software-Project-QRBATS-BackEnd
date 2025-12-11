package com.qrbats.qrbats.functionalities.location.service;

import com.qrbats.qrbats.entity.location.Location;
import com.qrbats.qrbats.entity.location.LocationRepository;
import com.qrbats.qrbats.entity.location.LocationCoordinates;
import com.qrbats.qrbats.functionalities.location.dto.CreateLocationRequest;
import com.qrbats.qrbats.functionalities.location.dto.LocationDto;
import com.qrbats.qrbats.functionalities.location.dto.LocationNameResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository repository;

    @Value("${attendance.allowableDistance}")
    private Double allowableDistance;

    @Transactional
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
        location.getLocationCoordinates().setLatitude(request.getLocationGPSLatitude());
        location.getLocationCoordinates().setLongitude(request.getLocationGPSLongitude());
        location.setAllowableRadius(request.getAllowableRadius());

        return repository.save(location);

    }

    public Location getLocationById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Location is not found for this id - " + id));
    }

    public boolean checkInTheLocation(LocationCoordinates locationCoordinates, Location location) {
        // Radius of the Earth in meters (6371 km * 1000 = 6371000 meters)
        final double R = 6371000.0;

        // Convert latitude and longitude from degrees to radians
        double latitudeDistance = Math.toRadians(location.getLocationCoordinates().getLatitude() - locationCoordinates.getLatitude());
        double longitudeDistance = Math.toRadians(location.getLocationCoordinates().getLongitude() - locationCoordinates.getLongitude());

        // Haversine formula
        double a = Math.sin(latitudeDistance / 2) * Math.sin(latitudeDistance / 2)
                + Math.cos(Math.toRadians(locationCoordinates.getLatitude())) * Math.cos(Math.toRadians(location.getLocationCoordinates().getLatitude()))
                * Math.sin(longitudeDistance / 2) * Math.sin(longitudeDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Calculate the distance in meters
        double distance = R * c;
        System.out.println("The distance is : " + distance);

        // Compare distance with allowable distance (in meters)
        return distance <= allowableDistance;
    }

    public List<String> getLocations() {
        return Arrays.asList(
                "50,6.07953758447428,80.19206030748484,NCC",
                "50,6.079133810214307,80.19155422805409,LT1",
                "50,6.078789414282487,80.1912795418734,LT2",
                "50,6.079015053021337,80.19112428446691,Auditorium",
                "50,6.079133810214307,80.19155422805409,DO1",
                "50,6.078789414282487,80.1912795418734,DO2",
                "50,6.078150890028423,80.19217574681655,DEIE",
                "50,6.078473748481698,80.19184438229699,DMME",
                "50,6.0781203313993455,80.19141948746949,DCEE",
                "50,6.077142619941645,80.19093951556941,Mechanical Workshop area",
                "50,6.079311039842098,80.19185410791715,OCC",
                "50,6.07953758447428,80.19206030748484,Seminar Room",
                "50,6.079493081707097,80.19168340470081,Library",
                "50,6.079493081707097,80.19168340470081,Study Hall",
                "50,6.079015053021337,80.19112428446691,NLH1",
                "50,6.079015053021337,80.19112428446691,NLH2",
                "50,6.079015053021337,80.19112428446691,NLH3",
                "50,6.079015053021337,80.19112428446691,NLH4"
        );
    }

    @Transactional
    public void addLocations() {
        List<String> locations = getLocations();

        // Load existing DB entities
        List<Location> existingEntities = repository.findAll();

        // Convert existing entities to a Map for quick lookup by name (or use composite key if needed)
        Map<String, Location> existingMap = existingEntities.stream()
                .collect(Collectors.toMap(Location::getLocationName, loc -> loc));

        for (String location : locations) {
            String[] values = location.split(",");

            double allowableRadius = Double.parseDouble(values[0]);
            double latitude = Double.parseDouble(values[1]);
            double longitude = Double.parseDouble(values[2]);
            String locationName = values[3];

            Location existing = existingMap.get(locationName);

            if (existing != null) {
                // Check if something changed → then update
                if (existing.getAllowableRadius() != allowableRadius ||
                        existing.getLocationCoordinates().getLatitude() != latitude ||
                        existing.getLocationCoordinates().getLongitude() != longitude) {

                    existing.setAllowableRadius(allowableRadius);
                    existing.setLocationCoordinates(new LocationCoordinates(latitude, longitude));
                    repository.save(existing);
                }
                // else: unchanged → do nothing
            } else {
                // New location → insert
                Location newEntity = new Location();
                newEntity.setAllowableRadius(allowableRadius);
                newEntity.setLocationCoordinates(new LocationCoordinates(latitude, longitude));
                newEntity.setLocationName(locationName);
                repository.save(newEntity);
            }
        }

        // Optional: delete locations from DB that are not in the incoming list
        Set<String> incomingNames = locations.stream()
                .map(loc -> loc.split(",")[3])
                .collect(Collectors.toSet());

        for (Location existing : existingEntities) {
            if (!incomingNames.contains(existing.getLocationName())) {
                repository.delete(existing);
            }
        }
    }


    public List<LocationDto> getAllLocation() {
        List<Location> locationList = repository.findAll();
        return locationList.stream()
                .map(location -> new LocationDto(location.getLocationId(), location.getLocationName()))
                .collect(Collectors.toList());
    }

    public String getLocationName(Integer locationId) {
        return repository.findById(locationId)
                .map(Location::getLocationName)
                .orElse("");
    }

}
