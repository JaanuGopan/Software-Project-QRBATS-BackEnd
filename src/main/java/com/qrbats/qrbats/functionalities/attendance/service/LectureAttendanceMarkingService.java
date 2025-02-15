package com.qrbats.qrbats.functionalities.attendance.service;

import com.qrbats.qrbats.authentication.entities.user.User;
import com.qrbats.qrbats.authentication.entities.user.repository.UserRepository;
import com.qrbats.qrbats.entity.attendance.AttendanceEvent;
import com.qrbats.qrbats.entity.attendance.AttendanceLecture;
import com.qrbats.qrbats.entity.lecture.Lecture;
import com.qrbats.qrbats.entity.lecture.LectureRepository;
import com.qrbats.qrbats.entity.location.Location;
import com.qrbats.qrbats.entity.location.LocationRepository;
import com.qrbats.qrbats.entity.module.Module;
import com.qrbats.qrbats.entity.module.ModuleRepository;
import com.qrbats.qrbats.entity.moduleenrolment.ModuleEnrolment;
import com.qrbats.qrbats.functionalities.attendance.dto.*;
import com.qrbats.qrbats.functionalities.module_creation.services.ModuleService;
import com.qrbats.qrbats.functionalities.module_creation.services.ModuleEnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LectureAttendanceMarkingService {

    private final ModuleRepository moduleRepository;
    private final UserRepository userRepository;
    private final LectureRepository lectureRepository;
    private final LocationRepository locationRepository;
    private final AttendanceLectureService attendanceLectureService;
    private final ModuleService moduleService;
    private final ModuleEnrollmentService moduleEnrollmentService;

    public boolean checkValidLocation(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0; // Radius of the Earth in kilometers
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // Convert to meters
        return distance <= 50.0; // Return true if within 30 meters
    }

    public AttendanceLecture markLectureAttendance(LectureAttendanceMarkingRequest request) {
        Module module = moduleRepository.findByModuleCode(request.getModuleCode()).orElseThrow(() -> new RuntimeException("Module Not Found."));
        User student = userRepository.findById(request.getStudentId()).orElseThrow(() -> new RuntimeException("Student Not Found"));

        if (!module.getDepartmentId().equals(student.getDepartmentId()) || !module.getSemester().equals(student.getSemester())) {
            throw new RuntimeException("Student Not Suit For This Module.");
        }

        Date sqlDate = request.getAttendedDate();
        java.util.Date utilDate = new java.util.Date(sqlDate.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("EEE");
        String dayOfWeek = sdf.format(utilDate);

        List<Lecture> lecturesList = lectureRepository.findAllByLectureModuleCodeAndLectureDay(request.getModuleCode(), dayOfWeek).orElseThrow(() -> new RuntimeException("There Are No Any Lectures For This Module For This Particular Day."));

        for (Lecture lecture : lecturesList) {
            if (lecture.getLectureStartTime().before(request.getAttendedTime()) && lecture.getLectureEndTime().after(request.getAttendedTime())) {

                Location location = locationRepository.findByLocationName(lecture.getLectureVenue()).orElseThrow(() -> new RuntimeException("Location Not Found"));

                boolean isValidLocation = checkValidLocation(location.getLocationGPSLatitude(), location.getLocationGPSLongitude(), request.getLatitude(), request.getLongitude());
                if (!isValidLocation) {
                    throw new RuntimeException("Student Location Is Not In The " + lecture.getLectureVenue() + ".");
                }

                if (!attendanceLectureService.getAttendanceByLectureIdAndStudentIdAndDate(lecture.getLectureId(), request.getStudentId(), request.getAttendedDate()).isEmpty()) {
                    throw new RuntimeException("The Attendance Already Marked For " + request.getAttendedDate() + " .");
                }

                AttendanceEvent attendanceEvent = new AttendanceEvent();
                attendanceEvent.setEventId(lecture.getLectureId());
                attendanceEvent.setAttendanceTime(request.getAttendedTime());
                attendanceEvent.setAttendanceDate(request.getAttendedDate());
                attendanceEvent.setAttendeeId(request.getStudentId());
                attendanceEvent.setAttendanceStatus(true);

                attendanceLectureService.saveLectureAttendance(lecture.getLectureId().toString(), attendanceEvent);

                return attendanceLectureService.getAttendanceByLectureIdAndStudentIdAndDate(lecture.getLectureId(), request.getStudentId(), request.getAttendedDate()).get(0);
            }
        }

        throw new RuntimeException("There Are No Any Lectures For The Module For This Time.");
    }

    public AttendanceLecture markLectureAttendanceByLectureId(LectureAttendanceMarkingByLectureIdRequest request) {
        Optional<Lecture> attendLecture = lectureRepository.findById(request.getLectureId());
        if (attendLecture.isEmpty()) throw new RuntimeException("There Is No Lecture For This Id");

        Module module = moduleRepository.findByModuleCode(attendLecture.get().getLectureModuleCode()).orElseThrow(() -> new RuntimeException("Module Not Found."));
        User student = userRepository.findById(request.getStudentId()).orElseThrow(() -> new RuntimeException("Student Not Found"));

        if (!module.getDepartmentId().equals(student.getDepartmentId()) || !module.getSemester().equals(student.getSemester())) {
            throw new RuntimeException("You Are Not Suit For This Module.");
        }

        boolean checkStudentModuleEnrollment = moduleEnrollmentService.checkStudentEnrollment(module.getModuleId(), student.getUserId());
        if (!checkStudentModuleEnrollment){
            throw new RuntimeException("You Are Not Enroll This Module "+module.getModuleCode()+".");
        }



        Lecture lecture = attendLecture.get();

        Date sqlDate = request.getAttendedDate();
        java.util.Date utilDate = new java.util.Date(sqlDate.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("EEE");
        String dayOfWeek = sdf.format(utilDate);
        if (!lecture.getLectureDay().equals(dayOfWeek)) throw new RuntimeException("The Lecture Date Is Not Match.");
        if (!(lecture.getLectureStartTime().before(request.getAttendedTime()) && lecture.getLectureEndTime().after(request.getAttendedTime()))) {
            throw new RuntimeException("The Time Is Not Match.");
        }
        Location location = locationRepository.findByLocationName(lecture.getLectureVenue()).orElseThrow(() -> new RuntimeException("Location Not Found"));

        boolean isValidLocation = checkValidLocation(location.getLocationGPSLatitude(), location.getLocationGPSLongitude(), request.getLatitude(), request.getLongitude());
        if (!isValidLocation) {
            throw new RuntimeException("Student Location Is Not In The " + lecture.getLectureVenue() + ".");
        }

        if (!attendanceLectureService.getAttendanceByLectureIdAndStudentIdAndDate(lecture.getLectureId(), request.getStudentId(), request.getAttendedDate()).isEmpty()) {
            throw new RuntimeException("The Attendance Already Marked For " + request.getAttendedDate() + " .");
        }

        AttendanceEvent attendanceEvent = new AttendanceEvent();
        attendanceEvent.setEventId(lecture.getLectureId());
        attendanceEvent.setAttendanceTime(request.getAttendedTime());
        attendanceEvent.setAttendanceDate(request.getAttendedDate());
        attendanceEvent.setAttendeeId(request.getStudentId());
        attendanceEvent.setAttendanceStatus(true);

        attendanceLectureService.saveLectureAttendance(lecture.getLectureId().toString(), attendanceEvent);

        return attendanceLectureService.getAttendanceByLectureIdAndStudentIdAndDate(lecture.getLectureId(), request.getStudentId(), request.getAttendedDate()).get(0);

    }

    public List<LectureAttendanceResponse> getAllAttendanceByModuleCode(String moduleCode) {
        Optional<Module> module = moduleRepository.findByModuleCode(moduleCode);
        if (module.isEmpty())
            throw new RuntimeException("There Is No Any Module Found For This " + moduleCode + " ModuleCode.");

        Optional<List<Lecture>> lectureList = lectureRepository.findAllByLectureModuleCode(moduleCode);
        if (lectureList.isEmpty()) throw new RuntimeException("There Is No Any Lectures For This ModuleCode.");

        List<LectureAttendanceResponse> lectureAttendanceResponseList = new ArrayList<>();
        for (Lecture lecture : lectureList.get()) {
            List<AttendanceLecture> attendanceLectures = attendanceLectureService.getAllAttendanceByLectureId(lecture.getLectureId());
            if (!attendanceLectures.isEmpty()) {
                for (AttendanceLecture attendanceLecture : attendanceLectures) {
                    Optional<User> student = userRepository.findById(attendanceLecture.getAttendeeId());
                    if (student.isPresent()) {
                        LectureAttendanceResponse lectureAttendanceResponse = new LectureAttendanceResponse();
                        lectureAttendanceResponse.setStudentId(student.get().getUserId());
                        lectureAttendanceResponse.setStudentName(student.get().getFirstName() + " " + student.get().getLastName());
                        lectureAttendanceResponse.setStudentIndexNumber(student.get().getIndexNumber());
                        lectureAttendanceResponse.setAttendanceStatus(attendanceLecture.getAttendanceStatus());
                        lectureAttendanceResponse.setAttendedDate(attendanceLecture.getAttendanceDate());
                        lectureAttendanceResponse.setAttendedTime(attendanceLecture.getAttendanceTime());

                        lectureAttendanceResponseList.add(lectureAttendanceResponse);
                    }
                }
            }
        }
        return lectureAttendanceResponseList;
    }

    public List<LectureAttendanceResponse> getAllAttendanceByLectureId(Integer lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new RuntimeException("Lecture Not Found."));

        List<User> enrolledStudentsList = moduleService.getAllEnrolledStudentByModuleCode(lecture.getLectureModuleCode());
        List<AttendanceLecture> allAttendanceByLectureId = attendanceLectureService.getAllAttendanceByLectureId(lectureId);

        List<LectureAttendanceResponse> lectureAttendanceResponseList = new ArrayList<>();

        for (AttendanceLecture attendanceLecture : allAttendanceByLectureId) {
            Optional<User> student = userRepository.findById(attendanceLecture.getAttendeeId());
            if (student.isPresent()) {
                LectureAttendanceResponse lectureAttendanceResponse = new LectureAttendanceResponse();
                lectureAttendanceResponse.setStudentId(attendanceLecture.getAttendeeId());
                lectureAttendanceResponse.setAttendedDate(attendanceLecture.getAttendanceDate());
                lectureAttendanceResponse.setAttendedTime(attendanceLecture.getAttendanceTime());
                lectureAttendanceResponse.setAttendanceStatus(attendanceLecture.getAttendanceStatus());
                lectureAttendanceResponse.setStudentName(student.get().getFirstName() + " " + student.get().getLastName());
                lectureAttendanceResponse.setStudentIndexNumber(student.get().getIndexNumber());
                lectureAttendanceResponseList.add(lectureAttendanceResponse);
            }

        }

        for (User enrolledStudent : enrolledStudentsList){
            boolean isAddNotAttendedStudent = true;
            for (AttendanceLecture attendanceLecture : allAttendanceByLectureId){
                if (attendanceLecture.getAttendeeId().equals(enrolledStudent.getUserId())){
                    isAddNotAttendedStudent = false;
                }
            }
            if (isAddNotAttendedStudent) {
                LectureAttendanceResponse lectureAttendanceResponse = new LectureAttendanceResponse();
                lectureAttendanceResponse.setStudentName(enrolledStudent.getFirstName() + " " + enrolledStudent.getLastName());
                lectureAttendanceResponse.setStudentIndexNumber(enrolledStudent.getIndexNumber());
                lectureAttendanceResponse.setStudentId(enrolledStudent.getUserId());
                lectureAttendanceResponse.setAttendanceStatus(false);
                lectureAttendanceResponseList.add(lectureAttendanceResponse);
            }
        }
        return lectureAttendanceResponseList;
    }


    public List<AttendanceLecture> getAllAttendanceForOneLecture(String moduleCode, Date day) {
        return null;
    }

    public List<LectureAttendanceResponse> getAllAttendanceByStudentId(Integer studentId) {
        Optional<User> student = userRepository.findById(studentId);
        if (student.isEmpty()) throw new RuntimeException("There Is No Student Found For This Id.");
        Optional<List<Module>> moduleList = moduleRepository.findAllByDepartmentId(student.get().getDepartmentId());
        if (moduleList.isEmpty()) throw new RuntimeException("There Are No Any Modules For The Student.");
        List<LectureAttendanceResponse> returnAttendanceList = new ArrayList<>();
        for (Module module : moduleList.get()) {
            List<LectureAttendanceResponse> attendanceListResponseList = getAllAttendanceByModuleCode(module.getModuleCode());
            if (!attendanceListResponseList.isEmpty()) {
                returnAttendanceList.addAll(attendanceListResponseList);
            }
        }
        return returnAttendanceList;
    }

    public List<AttendanceLectureHistoryResponse> getAllAttendanceHistoryByStudentId(Integer studentId) {
        Optional<User> student = userRepository.findById(studentId);
        if (student.isEmpty()) throw new RuntimeException("There Is No Student Found For This Id.");
        Optional<List<Module>> moduleList = moduleRepository.findAllBySemesterAndDepartmentId(
                student.get().getSemester(), student.get().getDepartmentId());
        if (moduleList.isEmpty()) throw new RuntimeException("There Are No Any Modules For The Student.");

        List<AttendanceLectureHistoryResponse> attendanceLectureHistoryResponseList = new ArrayList<>();
        for (Module module : moduleList.get()) {
            Optional<List<Lecture>> lectureList = lectureRepository.findAllByLectureModuleCode(module.getModuleCode());
            if (lectureList.isPresent()) {
                for (Lecture lecture : lectureList.get()) {
                    List<AttendanceLecture> attendanceLectureList = attendanceLectureService
                            .getAttendanceByLectureIdAndStudentId(lecture.getLectureId(), studentId);
                    if (!attendanceLectureList.isEmpty()) {
                        for (AttendanceLecture attendanceLecture : attendanceLectureList) {
                            AttendanceLectureHistoryResponse response = new AttendanceLectureHistoryResponse();
                            response.setLectureName(lecture.getLectureName());
                            response.setLectureModuleName(module.getModuleName());
                            response.setLectureModuleCode(module.getModuleCode());
                            response.setAttendedTime(attendanceLecture.getAttendanceTime());
                            response.setAttendedDate(attendanceLecture.getAttendanceDate());
                            response.setAttendanceStatus(attendanceLecture.getAttendanceStatus());

                            attendanceLectureHistoryResponseList.add(response);
                        }
                    }
                }
            }
        }
        return attendanceLectureHistoryResponseList;
    }

    public List<StudentOverallAttendanceResponse> getAllStudentsAttendanceReportByModuleId(Integer moduleId) {
        Optional<Module> module = moduleRepository.findById(moduleId);
        if (module.isEmpty()) throw new RuntimeException("Module Not Found For This Id.");

        Optional<List<Lecture>> allLectureList = lectureRepository.findAllByLectureModuleCode(module.get().getModuleCode());
        if (allLectureList.isEmpty()) throw new RuntimeException("There Is No Lecture For This Module");
        List<ModuleEnrolment> moduleEnrolmentList = moduleEnrollmentService.getModuleEnrolmentListByModuleId(moduleId);
        if (moduleEnrolmentList.isEmpty()) throw new RuntimeException("No One Enroll This Module");

        List<StudentOverallAttendanceResponse> responseList = new ArrayList<>();

        for (ModuleEnrolment enrolment : moduleEnrolmentList){
            Optional<User> student = userRepository.findById(enrolment.getStudentId());
            if (student.isPresent()){
                StudentOverallAttendanceResponse response = new StudentOverallAttendanceResponse();
                response.setStudentId(student.get().getUserId());
                response.setStudentName(student.get().getFirstName() + " " + student.get().getLastName());
                response.setIndexNumber(student.get().getIndexNumber());

                Integer attendedLecture = 0;
                Integer totalLecture = 0;
                for (Lecture lecture : allLectureList.get()){
                    List<AttendanceLecture> attendanceLecture = attendanceLectureService.getAttendanceByLectureIdAndStudentId(lecture.getLectureId(),student.get().getUserId());
                    if (!attendanceLecture.isEmpty()){
                        attendedLecture++;
                    }
                    totalLecture = totalLecture + attendanceLectureService.getLectureCountByLectureId(lecture.getLectureId());
                }

                response.setAttendedLectureCount(attendedLecture);
                response.setMissedLectureCount(totalLecture - attendedLecture);
                Double percentage = (double) attendedLecture / totalLecture * 100;
                response.setAttendancePercentage(percentage);

                responseList.add(response);
            }
        }

        return responseList;
    }

    public List<LectureWithDateResponse> getAllLectureWithDateForDayLecture(Integer lectureId) {
        Optional<Lecture> lecture = lectureRepository.findById(lectureId);
        if (!lecture.isPresent()) throw new RuntimeException("No Lecture Found For This Lecture Id.");

        List<Date> lectureDateList = attendanceLectureService.getLecturesByLectureId(lectureId);
        if (lectureDateList.isEmpty()) throw new RuntimeException("There Is No Any Attendance For This Lecture.");

        List<LectureWithDateResponse> responseList = new ArrayList<>();
        for (Date date : lectureDateList){
            LectureWithDateResponse response = new LectureWithDateResponse();
            response.setLectureDate(date);
            response.setLectureId(lectureId);
            response.setLectureStartTime(lecture.get().getLectureStartTime());
            response.setLectureEndTime(lecture.get().getLectureEndTime());
            response.setLectureModuleCode(lecture.get().getLectureModuleCode());
            response.setLectureVenue(lecture.get().getLectureVenue());
            response.setLectureName(lecture.get().getLectureName());

            responseList.add(response);
        }

        return responseList;
    }

    public List<LectureAttendanceResponse> getAllAttendanceForLectureWithDate(Integer lectureId, Date lectureDate) {
        Optional<Lecture> lecture = lectureRepository.findById(lectureId);
        if (!lecture.isPresent()) throw new RuntimeException("No Lecture Found For This Lecture Id.");

        List<Date> lectureDateList = attendanceLectureService.getLecturesByLectureId(lectureId);
        if (lectureDateList.isEmpty()) throw new RuntimeException("There Is No Any Attendance For This Lecture.");
        if (!lectureDateList.contains(lectureDate)) throw new RuntimeException("The Date Is Not Valid.");

        List<AttendanceLecture> attendanceLectureList = attendanceLectureService.getAttendanceByLectureIdAndDate(lectureId,lectureDate);

        List<User> enrolledStudentsList = moduleService.getAllEnrolledStudentByModuleCode(lecture.get().getLectureModuleCode());

        List<LectureAttendanceResponse> lectureAttendanceResponseList = new ArrayList<>();

        for (AttendanceLecture attendanceLecture : attendanceLectureList) {
            Optional<User> student = userRepository.findById(attendanceLecture.getAttendeeId());
            if (student.isPresent()) {
                LectureAttendanceResponse lectureAttendanceResponse = new LectureAttendanceResponse();
                lectureAttendanceResponse.setStudentId(attendanceLecture.getAttendeeId());
                lectureAttendanceResponse.setAttendedDate(attendanceLecture.getAttendanceDate());
                lectureAttendanceResponse.setAttendedTime(attendanceLecture.getAttendanceTime());
                lectureAttendanceResponse.setAttendanceStatus(attendanceLecture.getAttendanceStatus());
                lectureAttendanceResponse.setStudentName(student.get().getFirstName() + " " + student.get().getLastName());
                lectureAttendanceResponse.setStudentIndexNumber(student.get().getIndexNumber());
                lectureAttendanceResponseList.add(lectureAttendanceResponse);
            }

        }

        for (User enrolledStudent : enrolledStudentsList){
            boolean isAddNotAttendedStudent = true;
            for (AttendanceLecture attendanceLecture : attendanceLectureList){
                if (attendanceLecture.getAttendeeId().equals(enrolledStudent.getUserId())){
                    isAddNotAttendedStudent = false;
                }
            }
            if (isAddNotAttendedStudent) {
                LectureAttendanceResponse lectureAttendanceResponse = new LectureAttendanceResponse();
                lectureAttendanceResponse.setStudentName(enrolledStudent.getFirstName() + " " + enrolledStudent.getLastName());
                lectureAttendanceResponse.setStudentIndexNumber(enrolledStudent.getIndexNumber());
                lectureAttendanceResponse.setStudentId(enrolledStudent.getUserId());
                lectureAttendanceResponse.setAttendanceStatus(false);
                lectureAttendanceResponseList.add(lectureAttendanceResponse);
            }
        }

        return lectureAttendanceResponseList;
    }
}
