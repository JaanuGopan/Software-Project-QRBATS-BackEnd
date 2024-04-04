package com.qrbats.qrbats.functionalities.locationfunc.controller;

import com.qrbats.qrbats.entity.location.Location;
import com.qrbats.qrbats.functionalities.locationfunc.dto.CreateLocationRequest;
import com.qrbats.qrbats.functionalities.locationfunc.dto.LocationDistanceRequest;
import com.qrbats.qrbats.functionalities.locationfunc.service.LocationService;
import com.qrbats.qrbats.functionalities.locationfunc.service.impl.LocationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/location")
@CrossOrigin("http://localhost:3000")
@RequiredArgsConstructor
public class LocationController {

    private final LocationServiceImpl locationService;

    @PostMapping("createlocation")
    public ResponseEntity<Location> createLocation(@RequestBody CreateLocationRequest request){
         return ResponseEntity.ok(locationService.createLocation(request));
    }

    @PostMapping("/getdistance")
    public ResponseEntity<Double> getDistance(@RequestBody LocationDistanceRequest request){
        return ResponseEntity.ok(locationService.getDistance(request));
    }

}
