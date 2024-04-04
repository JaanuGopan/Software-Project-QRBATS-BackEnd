package com.qrbats.qrbats.functionalities.attendance.service.impl;

import com.qrbats.qrbats.authentication.entities.student.Student;
import com.qrbats.qrbats.authentication.entities.student.repository.StudentRepository;
import com.qrbats.qrbats.entity.attendance.Attendance;
import com.qrbats.qrbats.entity.attendance.AttendanceRepository;
import com.qrbats.qrbats.entity.event.Event;
import com.qrbats.qrbats.entity.event.EventRepository;
import com.qrbats.qrbats.entity.location.Location;
import com.qrbats.qrbats.entity.location.LocationRepository;
import com.qrbats.qrbats.functionalities.attendance.dto.AttendaceMarkingRequest;
import com.qrbats.qrbats.functionalities.attendance.dto.AttendanceListResponce;
import com.qrbats.qrbats.functionalities.attendance.dto.AttendanceStudentHistoryResponse;
import com.qrbats.qrbats.functionalities.attendance.service.AttendanceMarkingService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class AttendanceMarkingServiceImpl implements AttendanceMarkingService {

    private final AttendanceRepository attendanceRepository;
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final StudentRepository studentRepository;


    public boolean distanceCalculator(double lat1, double lon1, double lat2, double lon2) {
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
        return distance <= 30.0;
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

    @Override
    public Attendance markAttendance(AttendaceMarkingRequest attendaceMarkingRequest){

        Optional<Student> student = studentRepository.findById(attendaceMarkingRequest.getAttendeeID());
        Optional<Event> event = eventRepository.findById(attendaceMarkingRequest.getEventID());
        if (student.isPresent() && event.isPresent()) {
            if (
                    (event.get().getEventDate().isEqual(attendaceMarkingRequest.getAttendanceDate()) ||
                            event.get().getEventValidDate().isEqual(attendaceMarkingRequest.getAttendanceDate()) ||
                            (event.get().getEventValidDate().isBefore(attendaceMarkingRequest.getAttendanceDate()) &&
                                    event.get().getEventDate().isAfter(attendaceMarkingRequest.getAttendanceDate()))
                    ) &&
                    event.get().getEventEndTime().isAfter(attendaceMarkingRequest.getAttendanceTime())  &&
                    event.get().getEventTime().isBefore(attendaceMarkingRequest.getAttendanceTime())
            ) {
                Optional<Attendance> markedAttendance = attendanceRepository.findByEventIdAndAttendeeId(
                        attendaceMarkingRequest.getEventID(),
                        attendaceMarkingRequest.getAttendeeID()
                );
                if (markedAttendance.isPresent()){
                    throw new RuntimeException("The Student already mark the attendance.");
                }else {
                    List<Double> locationCoordinate = findEventLocation(attendaceMarkingRequest.getEventID());
                    if(!locationCoordinate.isEmpty()){
                        double locationGPSLatitude = locationCoordinate.get(0);
                        double locationGPSLongitude = locationCoordinate.get(1);

                        boolean isDistanceInRage = distanceCalculator(
                                locationGPSLatitude,
                                locationGPSLongitude,
                                attendaceMarkingRequest.getLocationGPSLatitude(),
                                attendaceMarkingRequest.getLocationGPSLongitude()
                        );
                        if(isDistanceInRage){
                            Attendance attendance = getAttendance(attendaceMarkingRequest);
                            return attendanceRepository.save(attendance);
                        }else {
                            throw new RuntimeException("The attendee in out of range.");
                        }

                    }else {
                        throw new RuntimeException("The location not found.");
                    }

                }
            }else if(
                    event.get().getEventEndTime().isBefore(attendaceMarkingRequest.getAttendanceTime())  &&
                            event.get().getEventTime().isAfter(attendaceMarkingRequest.getAttendanceTime())
            ) {
                throw new RuntimeException("Event Date not satisfied.");
            }else {
                throw new RuntimeException("Event Time not satisfied.");
            }
        }else if (event.isPresent()){
            throw new RuntimeException("Student not found.");
        }else {
            throw new RuntimeException("Event not found.");
        }

    }

    private static Attendance getAttendance(AttendaceMarkingRequest attendaceMarkingRequest) {
        Attendance attendance = new Attendance();
        attendance.setEventId(attendaceMarkingRequest.getEventID());
        attendance.setAttendeeId(attendaceMarkingRequest.getAttendeeID());
        attendance.setAttendanceDate(attendaceMarkingRequest.getAttendanceDate());
        attendance.setAttendanceTime(attendaceMarkingRequest.getAttendanceTime());
        attendance.setLocationLatitude(attendaceMarkingRequest.getLocationGPSLatitude());
        attendance.setLocationLongitude(attendaceMarkingRequest.getLocationGPSLongitude());
        return attendance;
    }

    @Override
    public List<AttendanceListResponce> getAllAttendanceByEventId(Integer eventId) {
        Optional<List<Attendance>> attendanceList = attendanceRepository.findAllByEventId(eventId);
        if(attendanceList.isPresent()){
            List<AttendanceListResponce> attendedStudentList = new ArrayList<>();
            for (Attendance attendance : attendanceList.get()) {
                Optional<Student> student = studentRepository.findById(attendance.getAttendeeId());
                if (student.isPresent()){
                    AttendanceListResponce response = new AttendanceListResponce();
                    String studentName = student.get().getStudentName();
                    String indexNumber = student.get().getIndexNumber();
                    response.setStudentName(studentName);
                    response.setAttendedDate(attendance.getAttendanceDate());
                    response.setAttendedTime(attendance.getAttendanceTime());
                    response.setIndexNumber(indexNumber);

                    attendedStudentList.add(response);
                }else {
                    throw new RuntimeException("Invalid Detail in the attendance table.");
                }


            }
            return attendedStudentList;
        }else {
            throw new RuntimeException("Attendance list not found.");
        }
    }

    @Override
    public List<AttendanceStudentHistoryResponse> getAllAttendanceByStudentId(Integer studentId) {
        Optional<Student> student = studentRepository.findById(studentId);
        if (student.isPresent()){
            Optional<List<Attendance>> attendanceList = attendanceRepository.findAllByAttendeeId(studentId);
            if (attendanceList.isPresent()){

                List<AttendanceStudentHistoryResponse> attendanceStudentHistoryResponsesList = new ArrayList<>();
                for (Attendance attendance : attendanceList.get()){
                    AttendanceStudentHistoryResponse response = new AttendanceStudentHistoryResponse();
                    Optional<Event> event = eventRepository.findById(attendance.getEventId());
                    if (event.isPresent()){
                        response.setEventName(event.get().getEventName());
                        response.setAttendedTime(attendance.getAttendanceTime());
                        response.setAttendedDate(attendance.getAttendanceDate());
                        attendanceStudentHistoryResponsesList.add(response);
                    }else {
                        throw new RuntimeException("Event not found.");
                    }
                }
                return attendanceStudentHistoryResponsesList;
            }else {
                throw new RuntimeException("No any attendance found for the student Id");
            }
        }else {
            throw new RuntimeException("There is no any student for given student id");
        }
    }


}
