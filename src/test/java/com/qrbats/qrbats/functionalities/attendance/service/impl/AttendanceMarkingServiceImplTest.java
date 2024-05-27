package com.qrbats.qrbats.functionalities.attendance.service.impl;

import com.qrbats.qrbats.authentication.entities.student.Student;
import com.qrbats.qrbats.authentication.entities.student.StudentRole;
import com.qrbats.qrbats.authentication.entities.student.repository.StudentRepository;
import com.qrbats.qrbats.entity.attendance.Attendance;
import com.qrbats.qrbats.entity.attendance.AttendanceRepository;
import com.qrbats.qrbats.entity.event.Event;
import com.qrbats.qrbats.entity.event.EventRepository;
import com.qrbats.qrbats.entity.event.EventRole;
import com.qrbats.qrbats.entity.location.Location;
import com.qrbats.qrbats.entity.location.LocationRepository;
import com.qrbats.qrbats.functionalities.attendance.dto.AttendanceMarkingRequest;
import com.qrbats.qrbats.functionalities.attendance.service.AttendanceMarkingService;
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
class AttendanceMarkingServiceImplTest {

    @Autowired
    private AttendanceMarkingService underTestAttendanceMarkingService;
    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private LocationRepository locationRepository;

    @AfterEach
    void tearDown() {
        attendanceRepository.deleteAll();
        studentRepository.deleteAll();
        eventRepository.deleteAll();
        locationRepository.deleteAll();
    }

    private Attendance getSampleAttendance(){
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
        Attendance attendance = new Attendance();
        attendance.setAttendanceDate(LocalDate.now());
        attendance.setAttendanceTime(LocalTime.parse("08:35:00"));
        attendance.setLocationLongitude(10.10);
        attendance.setLocationLatitude(10.20);
        attendance.setAttendeeId(savedStudent.getStudentId());
        attendance.setEventId(savedEvent.getEventId());

        return attendance;
    }

    private void deleteSampleAttendance(Attendance attendance){
        attendanceRepository.delete(attendance);
    }

    @Test
    void testValidMarkAttendance() {
        // given
        Attendance sampleAttendance = getSampleAttendance();
        AttendanceMarkingRequest attendaceMarkingRequest = new AttendanceMarkingRequest();
        attendaceMarkingRequest.setAttendanceDate(sampleAttendance.getAttendanceDate());
        attendaceMarkingRequest.setAttendanceTime(sampleAttendance.getAttendanceTime());
        attendaceMarkingRequest.setLocationGPSLongitude(sampleAttendance.getLocationLongitude());
        attendaceMarkingRequest.setLocationGPSLatitude(sampleAttendance.getLocationLatitude());
        attendaceMarkingRequest.setEventID(sampleAttendance.getEventId());
        attendaceMarkingRequest.setAttendeeID(sampleAttendance.getAttendeeId());

        // when
        Attendance expectedAttendance = underTestAttendanceMarkingService.markAttendance(attendaceMarkingRequest);

        // then
        assertThat(expectedAttendance).isNotNull();
        assertThat(expectedAttendance).isEqualTo(attendanceRepository.findById(expectedAttendance.getAttendanceId()).get());
    }
    @Test
    void testWithInvalidEventIdMarkAttendance() {
        // given
        Attendance sampleAttendance = getSampleAttendance();
        AttendanceMarkingRequest attendaceMarkingRequest = new AttendanceMarkingRequest();
        attendaceMarkingRequest.setAttendanceDate(sampleAttendance.getAttendanceDate());
        attendaceMarkingRequest.setAttendanceTime(sampleAttendance.getAttendanceTime());
        attendaceMarkingRequest.setLocationGPSLongitude(sampleAttendance.getLocationLongitude());
        attendaceMarkingRequest.setLocationGPSLatitude(sampleAttendance.getLocationLatitude());
        attendaceMarkingRequest.setEventID(sampleAttendance.getEventId()+99999);
        attendaceMarkingRequest.setAttendeeID(sampleAttendance.getAttendeeId());


        // then
        assertThrows(RuntimeException.class, () -> {
            underTestAttendanceMarkingService.markAttendance(attendaceMarkingRequest);
        });
    }

    @Test
    void testWithInvalidAttendeeIdMarkAttendance() {
        // given
        Attendance sampleAttendance = getSampleAttendance();
        AttendanceMarkingRequest attendaceMarkingRequest = new AttendanceMarkingRequest();
        attendaceMarkingRequest.setAttendanceDate(sampleAttendance.getAttendanceDate());
        attendaceMarkingRequest.setAttendanceTime(sampleAttendance.getAttendanceTime());
        attendaceMarkingRequest.setLocationGPSLongitude(sampleAttendance.getLocationLongitude());
        attendaceMarkingRequest.setLocationGPSLatitude(sampleAttendance.getLocationLatitude());
        attendaceMarkingRequest.setEventID(sampleAttendance.getEventId());
        attendaceMarkingRequest.setAttendeeID(sampleAttendance.getAttendeeId()+999999);


        // then
        assertThrows(RuntimeException.class, () -> {
            underTestAttendanceMarkingService.markAttendance(attendaceMarkingRequest);
        });
    }

    @Test
    void testWithInvalidDateMarkAttendance() {
        // given
        Attendance sampleAttendance = getSampleAttendance();
        AttendanceMarkingRequest attendaceMarkingRequest = new AttendanceMarkingRequest();
        attendaceMarkingRequest.setAttendanceDate(sampleAttendance.getAttendanceDate().plusDays(5));
        attendaceMarkingRequest.setAttendanceTime(sampleAttendance.getAttendanceTime());
        attendaceMarkingRequest.setLocationGPSLongitude(sampleAttendance.getLocationLongitude());
        attendaceMarkingRequest.setLocationGPSLatitude(sampleAttendance.getLocationLatitude());
        attendaceMarkingRequest.setEventID(sampleAttendance.getEventId());
        attendaceMarkingRequest.setAttendeeID(sampleAttendance.getAttendeeId());


        // then
        assertThrows(RuntimeException.class, () -> {
            underTestAttendanceMarkingService.markAttendance(attendaceMarkingRequest);
        });
    }

    @Test
    void testWithInvalidTimeMarkAttendance() {
        // given
        Attendance sampleAttendance = getSampleAttendance();
        AttendanceMarkingRequest attendaceMarkingRequest = new AttendanceMarkingRequest();
        attendaceMarkingRequest.setAttendanceDate(sampleAttendance.getAttendanceDate());
        attendaceMarkingRequest.setAttendanceTime(sampleAttendance.getAttendanceTime().plusHours(2));
        attendaceMarkingRequest.setLocationGPSLongitude(sampleAttendance.getLocationLongitude());
        attendaceMarkingRequest.setLocationGPSLatitude(sampleAttendance.getLocationLatitude());
        attendaceMarkingRequest.setEventID(sampleAttendance.getEventId());
        attendaceMarkingRequest.setAttendeeID(sampleAttendance.getAttendeeId());


        // then
        assertThrows(RuntimeException.class, () -> {
            underTestAttendanceMarkingService.markAttendance(attendaceMarkingRequest);
        });
    }
    @Test
    void testWithInvalidLocationsMarkAttendance() {
        // given
        Attendance sampleAttendance = getSampleAttendance();
        AttendanceMarkingRequest attendaceMarkingRequest = new AttendanceMarkingRequest();
        attendaceMarkingRequest.setAttendanceDate(sampleAttendance.getAttendanceDate());
        attendaceMarkingRequest.setAttendanceTime(sampleAttendance.getAttendanceTime());
        attendaceMarkingRequest.setLocationGPSLongitude(sampleAttendance.getLocationLongitude()+1.0);
        attendaceMarkingRequest.setLocationGPSLatitude(sampleAttendance.getLocationLatitude()-1.0);
        attendaceMarkingRequest.setEventID(sampleAttendance.getEventId());
        attendaceMarkingRequest.setAttendeeID(sampleAttendance.getAttendeeId());


        // then
        assertThrows(RuntimeException.class, () -> {
            underTestAttendanceMarkingService.markAttendance(attendaceMarkingRequest);
        });
    }


    


    @Test
    void getAllAttendanceByEventId() {

        // given
        Attendance sampleAttendance = attendanceRepository.save(getSampleAttendance());
        Integer eventId = 2;
        

    }

    @Test
    void getAllAttendanceByStudentId() {
    }
}