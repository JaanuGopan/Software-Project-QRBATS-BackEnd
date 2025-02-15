package com.qrbats.qrbats.functionalities.locationfunc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qrbats.qrbats.entity.location.Location;
import com.qrbats.qrbats.functionalities.locationfunc.dto.CreateLocationRequest;
import com.qrbats.qrbats.functionalities.locationfunc.dto.LocationDistanceRequest;
import com.qrbats.qrbats.functionalities.locationfunc.dto.LocationNameResponse;
import com.qrbats.qrbats.functionalities.locationfunc.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/location")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LocationController {

    private final LocationService locationService;

    @PostMapping("create-location")
    public ResponseEntity<Location> createLocation(@RequestBody CreateLocationRequest request){
         return ResponseEntity.ok(locationService.createLocation(request));
    }

    @PostMapping("/get-distance")
    public ResponseEntity<Double> getDistance(@RequestBody LocationDistanceRequest request){
        return ResponseEntity.ok(locationService.getDistance(request));
    }

    @GetMapping("/get-all-location-names")
    public ResponseEntity<List<LocationNameResponse>> getAllLocationNames(){
        return ResponseEntity.ok(locationService.getAllLocationName());
    }

}
