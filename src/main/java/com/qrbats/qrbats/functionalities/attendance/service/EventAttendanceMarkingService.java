package com.qrbats.qrbats.functionalities.attendance.service;

import com.qrbats.qrbats.authentication.entities.user.User;
import com.qrbats.qrbats.authentication.entities.user.repository.UserRepository;
import com.qrbats.qrbats.entity.attendance.AttendanceEvent;
import com.qrbats.qrbats.entity.event.Event;
import com.qrbats.qrbats.entity.event.EventRepository;
import com.qrbats.qrbats.entity.location.Location;
import com.qrbats.qrbats.entity.location.LocationRepository;
import com.qrbats.qrbats.functionalities.attendance.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventAttendanceMarkingService {

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    private final AttendanceEventService attendanceEventService;

    public boolean checkValidLocation(double lat1, double lon1, double lat2, double lon2) {
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
        return distance <= 50.0;
    }

    public List<Double> findEventLocation(int eventId){
        Optional<Event> event = eventRepository.findById(eventId);
        if(event.isPresent()){
            Optional<Location> location = locationRepository.findByLocationName(event.get().getEventVenue());
            if(location.isPresent()){
                List<Double> locationCoordinate = new ArrayList<>();
                locationCoordinate.add(location.get().getLocationGPSLatitude());
                locationCoordinate.add(location.get().getLocationGPSLongitude());
                return locationCoordinate;
            }else {
                return null;
            }
        }else {
            return null;
        }

    }

    public AttendanceEvent markAttendance(AttendanceMarkingRequest attendanceMarkingRequest){

        Optional<User> student = userRepository.findById(attendanceMarkingRequest.getAttendeeId());
        Optional<Event> event = eventRepository.findById(attendanceMarkingRequest.getEventId());
        if (student.isEmpty()) throw new RuntimeException("Student Not Found.");
        if (event.isEmpty()) throw new RuntimeException("Event not found.");
        if (!event.get().getEventDate().isEqual(attendanceMarkingRequest.getAttendanceDate())){
            throw new RuntimeException("The Event Date And Your Attendance Date Are Not Match.");
        }

        List<AttendanceEvent> attendanceEventList = attendanceEventService.getAllAttendanceByEventId(
                attendanceMarkingRequest.getEventId());

        boolean existsAttendance = attendanceEventList.stream()
                .anyMatch(attendanceEvent -> (Objects.equals(attendanceEvent.getAttendeeId(), attendanceMarkingRequest.getAttendeeId()))
                        && (attendanceEvent.getAttendanceStatus().equals(true)));
        if (existsAttendance) throw new  RuntimeException("The Attendance Already Marked.");

        LocalTime eventStartTime = event.get().getEventTime();
        LocalTime eventEndTime = event.get().getEventEndTime();
        LocalTime attendedTime = attendanceMarkingRequest.getAttendanceTime();
        boolean isTimeOverlap = eventStartTime.isBefore(attendedTime) && eventEndTime.isAfter(attendedTime);
        if (!isTimeOverlap) throw new RuntimeException("Attended Time Is Not In The Event Duration");

        Optional<Location> location = locationRepository.findByLocationName(event.get().getEventVenue());
        if (location.isEmpty()) throw new RuntimeException("Location Not Found.");

        double locationLatitude = location.get().getLocationGPSLatitude();
        double locationLongitude = location.get().getLocationGPSLongitude();
        double studentLocationLatitude = attendanceMarkingRequest.getLocationGPSLatitude();
        double studentLocationLongitude = attendanceMarkingRequest.getLocationGPSLongitude();
        boolean isLocationValid = checkValidLocation(locationLatitude,locationLongitude,studentLocationLatitude,studentLocationLongitude);
        if (!isLocationValid) throw new RuntimeException("The Location Is Out Of Range For " + event.get().getEventName()+".");


        AttendanceEvent attendanceEvent = new AttendanceEvent();
        attendanceEvent.setAttendanceDate(Date.valueOf(attendanceMarkingRequest.getAttendanceDate()));
        attendanceEvent.setAttendanceTime(Time.valueOf(attendanceMarkingRequest.getAttendanceTime()));
        attendanceEvent.setAttendeeId(attendanceMarkingRequest.getAttendeeId());
        attendanceEvent.setEventId(event.get().getEventId());

        attendanceEventService.saveEventAttendance(attendanceMarkingRequest.getEventId(), attendanceEvent);

        return attendanceEventService.getAttendanceByEventIdAndStudentId(
                attendanceMarkingRequest.getEventId(), attendanceMarkingRequest.getAttendeeId()
        );
    }

    public List<AttendanceListResponse> getAllAttendanceByEventId(Integer eventId) {

        List<AttendanceEvent> attendanceEventList = attendanceEventService.getAllAttendanceByEventId(eventId);
        List<AttendanceListResponse> attendanceListResponseList = new ArrayList<>();

        for (AttendanceEvent attendanceEvent : attendanceEventList){
            Optional<User> student = userRepository.findById(attendanceEvent.getAttendeeId());
            if (student.isPresent()){
                AttendanceListResponse attendanceListResponse = new AttendanceListResponse();
                attendanceListResponse.setStudentName(student.get().getFirstName() + " " + student.get().getLastName());
                attendanceListResponse.setAttendedTime(attendanceEvent.getAttendanceTime());
                attendanceListResponse.setAttendedDate(attendanceEvent.getAttendanceDate());
                attendanceListResponse.setIndexNumber(student.get().getIndexNumber());
                attendanceListResponse.setAttendanceStatus(attendanceEvent.getAttendanceStatus());
                attendanceListResponseList.add(attendanceListResponse);
            }
        }

        return attendanceListResponseList;

    }

    public List<AttendanceEventHistoryResponse> getAllAttendanceHistoryByStudentId(Integer studentId) {

        Optional<User> student = userRepository.findById(studentId);
        if (student.isEmpty()) throw new RuntimeException("Student Not Found For This Id");

        List<Event> eventList = eventRepository.findAll();

        List<AttendanceEvent> attendanceEventList = new ArrayList<>();
        List<AttendanceEventHistoryResponse> attendanceEventHistoryResponses = new ArrayList<>();

        for (Event event : eventList){
            AttendanceEvent attendance = attendanceEventService.getStudentAttendanceByEventIdAndStudentId(event.getEventId(),studentId);
            attendanceEventList.add(attendance);

            if (attendance != null){
                AttendanceEventHistoryResponse response = new AttendanceEventHistoryResponse();
                response.setAttendanceStatus(attendance.getAttendanceStatus());
                response.setAttendedDate((Date) attendance.getAttendanceDate());
                response.setAttendedTime(attendance.getAttendanceTime());
                response.setEventName(event.getEventName());

                attendanceEventHistoryResponses.add(response);
            }
        }

        return attendanceEventHistoryResponses;
    }
}
