package com.qrbats.qrbats.functionalities.attendance.service.impl;

import com.qrbats.qrbats.authentication.entities.student.Student;
import com.qrbats.qrbats.authentication.entities.student.repository.StudentRepository;
import com.qrbats.qrbats.entity.attendance.AttendanceEvent;
import com.qrbats.qrbats.entity.attendance.AttendanceLecture;
import com.qrbats.qrbats.entity.lecture.Lecture;
import com.qrbats.qrbats.entity.lecture.LectureRepository;
import com.qrbats.qrbats.entity.location.Location;
import com.qrbats.qrbats.entity.location.LocationRepository;
import com.qrbats.qrbats.entity.module.Module;
import com.qrbats.qrbats.entity.module.ModuleRepository;
import com.qrbats.qrbats.functionalities.attendance.dto.LectureAttendanceMarkingRequest;
import com.qrbats.qrbats.functionalities.attendance.dto.LectureAttendanceResponse;
import com.qrbats.qrbats.functionalities.attendance.service.LectureAttendanceMarkingService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LectureAttendanceMarkingServiceImpl implements LectureAttendanceMarkingService {
    @Autowired
    private final ModuleRepository moduleRepository;
    @Autowired
    private final StudentRepository studentRepository;
    @Autowired
    private final LectureRepository lectureRepository;
    @Autowired
    private final LocationRepository locationRepository;
    @Autowired
    private final AttendanceLectureService attendanceLectureService;

    public boolean checkValidLocation(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0; // Radius of the Earth in kilometers
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // Convert to meters
        return distance <= 30.0; // Return true if within 30 meters
    }

    @Override
    public AttendanceLecture markLectureAttendance(LectureAttendanceMarkingRequest request) {
        Module module = moduleRepository.findByModuleCode(request.getModuleCode())
                .orElseThrow(() -> new RuntimeException("Module Not Found."));
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student Not Found"));

        if (!module.getDepartmentId().equals(student.getDepartmentId()) || !module.getSemester().equals(student.getCurrentSemester())){
            throw new RuntimeException("Student Not Suit For This Module.");
        }

        Date sqlDate = request.getAttendedDate();
        java.util.Date utilDate = new java.util.Date(sqlDate.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("EEE");
        String dayOfWeek = sdf.format(utilDate);

        List<Lecture> lecturesList = lectureRepository
                .findAllByLectureModuleCodeAndLectureDay(request.getModuleCode(), dayOfWeek)
                .orElseThrow(() -> new RuntimeException("There Are No Any Lectures For This Module For This Particular Day."));

        for (Lecture lecture : lecturesList) {
            if (lecture.getLectureStartTime().before(request.getAttendedTime())
                    && lecture.getLectureEndTime().after(request.getAttendedTime())) {

                Location location = locationRepository.findByLocationName(lecture.getLectureVenue())
                        .orElseThrow(() -> new RuntimeException("Location Not Found"));

                boolean isValidLocation = checkValidLocation(
                        location.getLocationGPSLatitude(), location.getLocationGPSLongitude(),
                        request.getLatitude(), request.getLongitude());
                if (!isValidLocation) {
                    throw new RuntimeException("Student Location Is Not In The "+lecture.getLectureVenue()+".");
                }

                if (!attendanceLectureService.getAttendanceByLectureIdAndStudentIdAndDate(lecture.getLectureId(), request.getStudentId(), request.getAttendedDate()).isEmpty()){
                    throw new RuntimeException("The Attendance Already Marked For "+request.getAttendedDate()+" .");
                }

                AttendanceEvent attendanceEvent = new AttendanceEvent();
                attendanceEvent.setEventId(lecture.getLectureId());
                attendanceEvent.setAttendanceTime(request.getAttendedTime());
                attendanceEvent.setAttendanceDate(request.getAttendedDate());
                attendanceEvent.setAttendeeId(request.getStudentId());
                attendanceEvent.setAttendanceStatus(true);

                attendanceLectureService.saveLectureAttendance(lecture.getLectureId().toString(), attendanceEvent);

                return attendanceLectureService.getAttendanceByLectureIdAndStudentIdAndDate(lecture.getLectureId(), request.getStudentId(),request.getAttendedDate()).get(0);
            }
        }

        throw new RuntimeException("There Are No Any Lectures For The Module For This Time.");
    }

    @Override
    public List<AttendanceLecture> getAllAttendanceByModuleCode(String moduleCode) {
        return null;
    }

    @Override
    public List<LectureAttendanceResponse> getAllAttendanceByLectureId(Integer lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() ->  new RuntimeException("Lecture Not Found."));

        List<AttendanceLecture> allAttendanceByLectureId =
                attendanceLectureService.getAllAttendanceByLectureId(lectureId);

        List<LectureAttendanceResponse> lectureAttendanceResponseList = new ArrayList<>();
        for (AttendanceLecture attendanceLecture: allAttendanceByLectureId){
            Optional<Student> student = studentRepository.findById(attendanceLecture.getAttendeeId());
            if (student.isPresent()){
                LectureAttendanceResponse lectureAttendanceResponse = new LectureAttendanceResponse();
                lectureAttendanceResponse.setStudentId(attendanceLecture.getAttendeeId());
                lectureAttendanceResponse.setAttendedDate(attendanceLecture.getAttendanceDate());
                lectureAttendanceResponse.setAttendedTime(attendanceLecture.getAttendanceTime());
                lectureAttendanceResponse.setAttendanceStatus(attendanceLecture.getAttendanceStatus());
                lectureAttendanceResponse.setStudentName(student.get().getStudentName());
                lectureAttendanceResponse.setStudentIndexNumber(student.get().getIndexNumber());
                lectureAttendanceResponseList.add(lectureAttendanceResponse);
            }

        }
        return lectureAttendanceResponseList;
    }


    @Override
    public List<AttendanceLecture> getAllAttendanceForOneLecture(String moduleCode, Date day) {
        return null;
    }
}
