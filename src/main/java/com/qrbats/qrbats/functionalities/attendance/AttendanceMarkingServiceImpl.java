package com.qrbats.qrbats.functionalities.attendance;

import com.qrbats.qrbats.entity.attendance.Attendance;
import com.qrbats.qrbats.entity.attendance.AttendanceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AttendanceMarkingServiceImpl implements AttendanceMarkingService {

    private final AttendanceRepository attendanceRepository;

    @Override
    public Attendance markAttendance(AttendaceMarkingRequest attendaceMarkingRequest){
        Attendance attendance = new Attendance();
        attendance.setEventId(attendaceMarkingRequest.getEventID());
        attendance.setAttendeeId(attendaceMarkingRequest.getAttendeeID());
        attendance.setAttendanceDate(attendaceMarkingRequest.getAttendanceDate());
        attendance.setAttendanceTime(attendaceMarkingRequest.getAttendanceTime());
        attendance.setLocationX(attendaceMarkingRequest.getLocationX());
        attendance.setLocationY(attendaceMarkingRequest.getLocationY());
        return attendanceRepository.save(attendance);
    }
}
