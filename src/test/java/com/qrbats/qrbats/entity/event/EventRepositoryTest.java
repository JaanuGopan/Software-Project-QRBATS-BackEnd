package com.qrbats.qrbats.entity.event;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;


@DataJpaTest
class EventRepositoryTest {
    @Autowired
    private EventRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findAllByEventModuleCode_ReturnsListOfEvents_WhenEventsExistWithGivenModuleCode() {
        // given
        String moduleCode = "EE5300";
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

        // when
        Optional<List<Event>> expected = underTest.findAllByEventModuleCode(moduleCode);

        // then
        assertThat(expected.isPresent()).isTrue();
        assertThat(expected.get()).containsExactlyInAnyOrderElementsOf(List.of(event1, event2));
    }

    @Test
    @Disabled
    void findByEventNameAndEventDateAndEventTimeAndEventModuleCode() {
    }

    @Test
    @Disabled
    void findAllByEventAssignedUserId() {
    }
}