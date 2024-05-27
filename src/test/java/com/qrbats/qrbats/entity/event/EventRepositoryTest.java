package com.qrbats.qrbats.entity.event;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;


@SpringBootTest
class EventRepositoryTest {
    @Autowired
    private EventRepository underTest;

   /* @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }*/

    @Test
    void findAllByEventModuleCode_ReturnsListOfEvents_WhenEventsExistWithGivenModuleCode() {
        // given
        String moduleCode = "EE5300";
        addSampleEvent();

        // when
        Optional<List<Event>> expected = underTest.findAllByEventModuleCode(moduleCode);

        // then
        assertThat(expected.isPresent()).isTrue();
        //assertThat(expected.get()).containsExactlyInAnyOrderElementsOf(List.of(event1, event2));

        deleteSampleEvent(expected);
    }

    private void deleteSampleEvent(Optional<List<Event>> expected) {
        for(Event event : expected.get()){
            underTest.delete(event);
        }
    }

    private void addSampleEvent() {
        Event event1 = new Event();
        event1.setEventModuleCode("EE5300");
        event1.setEventRole(EventRole.LECTURE);
        event1.setEventName("Test Lecture 1");
        event1.setEventDate(LocalDate.now());
        event1.setEventTime(LocalTime.now());
        event1.setEventEndTime(LocalTime.now().plusHours(2));
        event1.setEventVenue("NCC");
        event1.setEventAssignedUserId(2);
        event1.setEventValidDate(LocalDate.now().plusDays(1));
        underTest.save(event1);

        Event event2 = new Event();
        event2.setEventModuleCode("EE5300");
        event2.setEventRole(EventRole.LECTURE);
        event2.setEventName("Test Lecture 2");
        event2.setEventDate(LocalDate.now());
        event2.setEventTime(LocalTime.now());
        event2.setEventEndTime(LocalTime.now().plusHours(2));
        event2.setEventVenue("NCC");
        event2.setEventAssignedUserId(2);
        event2.setEventValidDate(LocalDate.now().plusDays(1));
        underTest.save(event2);
    }



    @Test
    void findAllByEventAssignedUserId() {
        // given
        int userId = 2;
        addSampleEvent();
        
        //when
        Optional<List<Event>> expectedList = underTest.findAllByEventAssignedUserId(userId);
        
        // then
        assertThat(expectedList.isPresent()).isTrue();
        for (Event event:expectedList.get()){
            assertThat(event.getEventAssignedUserId()).isEqualTo(2);
        }

        deleteSampleEvent(expectedList);

    }
}