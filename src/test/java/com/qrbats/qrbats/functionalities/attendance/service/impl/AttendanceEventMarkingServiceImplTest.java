/*
package com.qrbats.qrbats.functionalities.attendance.service.impl;

import com.qrbats.qrbats.authentication.entities.student.Student;
import com.qrbats.qrbats.authentication.entities.student.StudentRole;
import com.qrbats.qrbats.authentication.entities.student.repository.StudentRepository;
import com.qrbats.qrbats.entity.attendance.AttendanceEvent;
import com.qrbats.qrbats.entity.event.Event;
import com.qrbats.qrbats.entity.event.EventRepository;
import com.qrbats.qrbats.entity.event.EventRole;
import com.qrbats.qrbats.entity.location.Location;
import com.qrbats.qrbats.entity.location.LocationRepository;
import com.qrbats.qrbats.functionalities.attendance.dto.AttendanceMarkingRequest;
import com.qrbats.qrbats.functionalities.attendance.service.EventAttendanceMarkingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class AttendanceEventMarkingServiceImplTest {

    @Autowired
    private EventAttendanceMarkingService underTestEventAttendanceMarkingService;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private LocationRepository locationRepository;

    @AfterEach
    void tearDown() {
        studentRepository.deleteAll();
        eventRepository.deleteAll();
        locationRepository.deleteAll();
    }

    private AttendanceEvent getSampleAttendance(){
        // student
        Student student = new Student();
        student.setIndexNumber("EG/2020/1234");
        student.setStudentRole(StudentRole.UORSTUDENT);
        student.setDepartmentId(2);
        student.setStudentEmail("testStudentEmail@gmail.com");
        student.setStudentName("testStudentName");
        student.setUserName("testUserName");
        student.setPassword(new BCryptPasswordEncoder().encode("testPassword"));
        student.setCurrentSemester(6);
        Student savedStudent = studentRepository.save(student);

        // event
        Event event = new Event();
        event.setEventName("TestEvent");
        event.setEventVenue("TestLocation");
        event.setEventTime(LocalTime.parse("08:30:00"));
        event.setEventDate(LocalDate.now());
        event.setEventValidDate(LocalDate.now().plusDays(1));
        event.setEventEndTime(LocalTime.parse("08:30:00").plusHours(1));
        event.setEventAssignedUserId(2);
        event.setEventRole(EventRole.LECTURE);
        event.setEventModuleCode("EE1234");
        Event savedEvent = eventRepository.save(event);

        // location
        Location location = new Location();
        location.setLocationName("testLocation");
        location.setLocationGPSLongitude(10.10);
        location.setLocationGPSLatitude(10.20);
        location.setAllowableRadius(15);
        locationRepository.save(location);



        // attendance
        AttendanceEvent attendanceEvent = new AttendanceEvent();
        attendanceEvent.setAttendanceDate(LocalDate.now());
        attendanceEvent.setAttendanceTime(LocalTime.parse("08:35:00"));
        attendanceEvent.setLocationLongitude(10.10);
        attendanceEvent.setLocationLatitude(10.20);
        attendanceEvent.setAttendeeId(savedStudent.getStudentId());
        attendanceEvent.setEventId(savedEvent.getEventId());

        return attendanceEvent;
    }

    private void deleteSampleAttendance(AttendanceEvent attendanceEvent){
        attendanceRepository.delete(attendanceEvent);
    }

    @Test
    void testValidMarkAttendance() {
        // given
        AttendanceEvent sampleAttendanceEvent = getSampleAttendance();
        AttendanceMarkingRequest attendaceMarkingRequest = new AttendanceMarkingRequest();
        attendaceMarkingRequest.setAttendanceDate(sampleAttendanceEvent.getAttendanceDate());
        attendaceMarkingRequest.setAttendanceTime(sampleAttendanceEvent.getAttendanceTime());
        attendaceMarkingRequest.setLocationGPSLongitude(sampleAttendanceEvent.getLocationLongitude());
        attendaceMarkingRequest.setLocationGPSLatitude(sampleAttendanceEvent.getLocationLatitude());
        attendaceMarkingRequest.setEventID(sampleAttendanceEvent.getEventId());
        attendaceMarkingRequest.setAttendeeID(sampleAttendanceEvent.getAttendeeId());

        // when
        AttendanceEvent expectedAttendanceEvent = underTestEventAttendanceMarkingService.markAttendance(attendaceMarkingRequest);

        // then
        assertThat(expectedAttendanceEvent).isNotNull();
        assertThat(expectedAttendanceEvent).isEqualTo(attendanceRepository.findById(expectedAttendanceEvent.getAttendanceId()).get());
    }
    @Test
    void testWithInvalidEventIdMarkAttendance() {
        // given
        AttendanceEvent sampleAttendanceEvent = getSampleAttendance();
        AttendanceMarkingRequest attendaceMarkingRequest = new AttendanceMarkingRequest();
        attendaceMarkingRequest.setAttendanceDate(sampleAttendanceEvent.getAttendanceDate());
        attendaceMarkingRequest.setAttendanceTime(sampleAttendanceEvent.getAttendanceTime());
        attendaceMarkingRequest.setLocationGPSLongitude(sampleAttendanceEvent.getLocationLongitude());
        attendaceMarkingRequest.setLocationGPSLatitude(sampleAttendanceEvent.getLocationLatitude());
        attendaceMarkingRequest.setEventID(sampleAttendanceEvent.getEventId()+99999);
        attendaceMarkingRequest.setAttendeeID(sampleAttendanceEvent.getAttendeeId());


        // then
        assertThrows(RuntimeException.class, () -> {
            underTestEventAttendanceMarkingService.markAttendance(attendaceMarkingRequest);
        });
    }

    @Test
    void testWithInvalidAttendeeIdMarkAttendance() {
        // given
        AttendanceEvent sampleAttendanceEvent = getSampleAttendance();
        AttendanceMarkingRequest attendaceMarkingRequest = new AttendanceMarkingRequest();
        attendaceMarkingRequest.setAttendanceDate(sampleAttendanceEvent.getAttendanceDate());
        attendaceMarkingRequest.setAttendanceTime(sampleAttendanceEvent.getAttendanceTime());
        attendaceMarkingRequest.setLocationGPSLongitude(sampleAttendanceEvent.getLocationLongitude());
        attendaceMarkingRequest.setLocationGPSLatitude(sampleAttendanceEvent.getLocationLatitude());
        attendaceMarkingRequest.setEventID(sampleAttendanceEvent.getEventId());
        attendaceMarkingRequest.setAttendeeID(sampleAttendanceEvent.getAttendeeId()+999999);


        // then
        assertThrows(RuntimeException.class, () -> {
            underTestEventAttendanceMarkingService.markAttendance(attendaceMarkingRequest);
        });
    }

    @Test
    void testWithInvalidDateMarkAttendance() {
        // given
        AttendanceEvent sampleAttendanceEvent = getSampleAttendance();
        AttendanceMarkingRequest attendaceMarkingRequest = new AttendanceMarkingRequest();
        attendaceMarkingRequest.setAttendanceDate(sampleAttendanceEvent.getAttendanceDate().plusDays(5));
        attendaceMarkingRequest.setAttendanceTime(sampleAttendanceEvent.getAttendanceTime());
        attendaceMarkingRequest.setLocationGPSLongitude(sampleAttendanceEvent.getLocationLongitude());
        attendaceMarkingRequest.setLocationGPSLatitude(sampleAttendanceEvent.getLocationLatitude());
        attendaceMarkingRequest.setEventID(sampleAttendanceEvent.getEventId());
        attendaceMarkingRequest.setAttendeeID(sampleAttendanceEvent.getAttendeeId());


        // then
        assertThrows(RuntimeException.class, () -> {
            underTestEventAttendanceMarkingService.markAttendance(attendaceMarkingRequest);
        });
    }

    @Test
    void testWithInvalidTimeMarkAttendance() {
        // given
        AttendanceEvent sampleAttendanceEvent = getSampleAttendance();
        AttendanceMarkingRequest attendaceMarkingRequest = new AttendanceMarkingRequest();
        attendaceMarkingRequest.setAttendanceDate(sampleAttendanceEvent.getAttendanceDate());
        attendaceMarkingRequest.setAttendanceTime(sampleAttendanceEvent.getAttendanceTime().plusHours(2));
        attendaceMarkingRequest.setLocationGPSLongitude(sampleAttendanceEvent.getLocationLongitude());
        attendaceMarkingRequest.setLocationGPSLatitude(sampleAttendanceEvent.getLocationLatitude());
        attendaceMarkingRequest.setEventID(sampleAttendanceEvent.getEventId());
        attendaceMarkingRequest.setAttendeeID(sampleAttendanceEvent.getAttendeeId());


        // then
        assertThrows(RuntimeException.class, () -> {
            underTestEventAttendanceMarkingService.markAttendance(attendaceMarkingRequest);
        });
    }
    @Test
    void testWithInvalidLocationsMarkAttendance() {
        // given
        AttendanceEvent sampleAttendanceEvent = getSampleAttendance();
        AttendanceMarkingRequest attendaceMarkingRequest = new AttendanceMarkingRequest();
        attendaceMarkingRequest.setAttendanceDate(sampleAttendanceEvent.getAttendanceDate());
        attendaceMarkingRequest.setAttendanceTime(sampleAttendanceEvent.getAttendanceTime());
        attendaceMarkingRequest.setLocationGPSLongitude(sampleAttendanceEvent.getLocationLongitude()+1.0);
        attendaceMarkingRequest.setLocationGPSLatitude(sampleAttendanceEvent.getLocationLatitude()-1.0);
        attendaceMarkingRequest.setEventID(sampleAttendanceEvent.getEventId());
        attendaceMarkingRequest.setAttendeeID(sampleAttendanceEvent.getAttendeeId());


        // then
        assertThrows(RuntimeException.class, () -> {
            underTestEventAttendanceMarkingService.markAttendance(attendaceMarkingRequest);
        });
    }


    


    @Test
    void getAllAttendanceByEventId() {

        // given
        AttendanceEvent sampleAttendanceEvent = attendanceRepository.save(getSampleAttendance());
        Integer eventId = 2;
        

    }

    @Test
    void getAllAttendanceByStudentId() {
    }
}*/
