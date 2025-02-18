package com.qrbats.qrbats.entity.location;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LocationRepositoryTest {
    @Autowired
    private LocationRepository underTestLocationRepo;

    private Location addSampleLocation(){
        Location location = new Location();
        location.setLocationName("testLocation");
        location.setAllowableRadius(30.0);
        location.setLatitude(10.00);
        location.setLongitude(10.00);

        return underTestLocationRepo.save(location);
    }

    private void deleteSampleLocation(Location location){
        underTestLocationRepo.delete(location);
    }
    @Test
    void findByLocationName() {
        // given
        String locationName = "testLocation";
        Location sampleLocation = addSampleLocation();

        // when
        Optional<Location> expectedLocation = underTestLocationRepo.findByLocationName(locationName);

        // then
        assertThat(expectedLocation.isPresent()).isTrue();
        assertThat(expectedLocation.get()).isEqualTo(sampleLocation);

        deleteSampleLocation(sampleLocation);

    }
}