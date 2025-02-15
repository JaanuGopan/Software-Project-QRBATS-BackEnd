package com.qrbats.qrbats.functionalities.lecturecreation.service;

import com.qrbats.qrbats.authentication.entities.user.User;
import com.qrbats.qrbats.authentication.entities.user.repository.UserRepository;
import com.qrbats.qrbats.entity.event.Event;
import com.qrbats.qrbats.entity.event.EventRepository;
import com.qrbats.qrbats.functionalities.attendance.service.AttendanceLectureService;
import com.qrbats.qrbats.entity.lecture.Lecture;
import com.qrbats.qrbats.entity.lecture.LectureRepository;
import com.qrbats.qrbats.entity.location.LocationRepository;
import com.qrbats.qrbats.entity.module.Module;
import com.qrbats.qrbats.entity.module.ModuleRepository;
import com.qrbats.qrbats.functionalities.lecturecreation.dto.CreateLectureRequest;
import com.qrbats.qrbats.functionalities.lecturecreation.dto.TimeSlot;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LectureCreationService {

    private final LectureRepository lectureRepository;
    private final UserRepository userRepository;
    private final ModuleRepository moduleRepository;
    private final LocationRepository locationRepository;
    private final AttendanceLectureService attendanceLectureService;
    private final EventRepository eventRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public List<Lecture> createLecture(CreateLectureRequest createLectureRequest) {
        Optional<User> user = userRepository.findById(createLectureRequest.getLectureAssignedUserId());
        if (user.isEmpty()) {
            throw new RuntimeException("User not Found.");
        }

        Optional<Module> module = moduleRepository.findByModuleCode(createLectureRequest.getModuleCode());
        if (module.isEmpty()) {
            throw new RuntimeException("Module not Found.");
        }

        Map<String, List<TimeSlot>> timesMap = createLectureRequest.getTimes();
        List<Lecture> createdLectures = new ArrayList<>();

        for (Map.Entry<String, List<TimeSlot>> entry : timesMap.entrySet()) {
            String day = entry.getKey();
            List<TimeSlot> timeSlots = entry.getValue();

            for (TimeSlot timeSlot : timeSlots) {
                String startTime = timeSlot.getStartTime();
                String endTime = timeSlot.getEndTime();
                String venue = timeSlot.getVenue();

                if (!locationRepository.findByLocationName(venue).isPresent()) {
                    throw new RuntimeException("Venue " + venue + " not Found.");
                }

                // For Same Venue Same Day Same Time Lecture.
                Optional<List<Lecture>> existingLectures = lectureRepository.findAllByLectureDayAndLectureVenue(day, venue);
                for (Lecture lectureWithSameDayAndVenue : existingLectures.get()){
                    LocalTime lectureWithSameDayAndVenueStartTime = lectureWithSameDayAndVenue.getLectureStartTime().toLocalTime();
                    LocalTime lectureWithSameDayAndVenueEndTime = lectureWithSameDayAndVenue.getLectureEndTime().toLocalTime();
                    LocalTime lectureStartTime = LocalTime.parse(startTime);
                    LocalTime lectureEndTime = LocalTime.parse(endTime);

                    boolean isLectureStartAndEndTimesEqualsToLectureWithSameDayAndVenueStartAndEndTimes =
                            lectureStartTime.equals(lectureWithSameDayAndVenueStartTime) || lectureStartTime.equals(lectureWithSameDayAndVenueEndTime)
                                    || lectureEndTime.equals(lectureWithSameDayAndVenueStartTime) || lectureEndTime.equals(lectureWithSameDayAndVenueEndTime);
                    boolean isLectureStartTimeInLectureWithSameDayAndVenueTimeDuration = lectureStartTime.isAfter(lectureWithSameDayAndVenueStartTime) && lectureStartTime.isBefore(lectureWithSameDayAndVenueEndTime);
                    boolean isLectureEndTimeInLectureWithSameDayAndVenueTimeDuration = lectureEndTime.isAfter(lectureWithSameDayAndVenueStartTime) && lectureEndTime.isBefore(lectureWithSameDayAndVenueEndTime);
                    boolean isLectureWithSameDayAndVenueTimeDurationInLectureTimeDuration = lectureStartTime.isBefore(lectureWithSameDayAndVenueStartTime) && lectureEndTime.isAfter(lectureWithSameDayAndVenueEndTime);
                    if (isLectureStartAndEndTimesEqualsToLectureWithSameDayAndVenueStartAndEndTimes || isLectureStartTimeInLectureWithSameDayAndVenueTimeDuration || isLectureEndTimeInLectureWithSameDayAndVenueTimeDuration || isLectureWithSameDayAndVenueTimeDurationInLectureTimeDuration){
                        throw new RuntimeException("There is Another Lecture For The Venue-"+venue+" At The Same Time.");
                    }
                }


                // For OtherModuleLecturesAtSameTime For Students
                Optional<List<Module>> moduleList = moduleRepository.findAllBySemesterAndDepartmentId(module.get().getSemester(),module.get().getDepartmentId());
                List<Lecture> otherModuleLecturesAtSameTime = new ArrayList<>();
                for (Module  module1: moduleList.get()){
                    Optional<List<Lecture>> lectureList = lectureRepository.findAllByLectureModuleCode(module1.getModuleCode());
                    for (Lecture otherLecturesForStudent : lectureList.get()){
                        otherModuleLecturesAtSameTime.add(otherLecturesForStudent);
                    }
                }
                if (!otherModuleLecturesAtSameTime.isEmpty()){
                    for (Lecture otherModuleLecture : otherModuleLecturesAtSameTime){
                        String otherLectureDay = otherModuleLecture.getLectureDay();
                        LocalTime otherLectureStartTime = otherModuleLecture.getLectureStartTime().toLocalTime();
                        LocalTime otherLectureEndTime = otherModuleLecture.getLectureEndTime().toLocalTime();
                        LocalTime lectureStartTime = LocalTime.parse(startTime);
                        LocalTime lectureEndTime = LocalTime.parse(endTime);

                        boolean isSameDay = otherLectureDay.equals(day);
                        boolean isLectureStartAndEndTimesEqualsToOtherLectureStartAndEndTimes =
                                lectureStartTime.equals(otherLectureStartTime) || lectureStartTime.equals(otherLectureEndTime)
                                        || lectureEndTime.equals(otherLectureStartTime) || lectureEndTime.equals(otherLectureEndTime);
                        boolean isLectureStartTimeInOtherModuleLectureTimeDuration = lectureStartTime.isAfter(otherLectureStartTime) && lectureStartTime.isBefore(otherLectureEndTime);
                        boolean isLectureEndTimeInOtherModuleLectureTimeDuration = lectureEndTime.isAfter(otherLectureStartTime) && lectureEndTime.isBefore(otherLectureEndTime);
                        boolean isOtherModuleLectureTimeDurationInLectureTimeDuration = lectureStartTime.isBefore(otherLectureStartTime) && lectureEndTime.isAfter(otherLectureEndTime);
                        if (isSameDay) {
                            if (isLectureStartAndEndTimesEqualsToOtherLectureStartAndEndTimes || isLectureStartTimeInOtherModuleLectureTimeDuration || isLectureEndTimeInOtherModuleLectureTimeDuration || isOtherModuleLectureTimeDurationInLectureTimeDuration){
                                String[] departmentList = {"DEIE","DCOM","DMME","DCEE","DMENA","DIS"};
                                throw new RuntimeException("There Is Another Lecture For The Semester-"+module.get().getSemester()+" Department-"+departmentList[module.get().getDepartmentId()-1]+" Students.");
                            }
                        }
                    }
                }

                // For Solve Event Overlapping
                Optional<List<Event>> eventListByVenue = eventRepository.findAllByEventVenue(venue);
                List<Event> eventListByVenueAndDay = eventListByVenue.get().stream()
                        .filter(event -> event.getEventDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH).equals(day))
                        .collect(Collectors.toList());
                for (Event eventByVenueAndDay : eventListByVenueAndDay){
                    LocalTime eventStartTime = eventByVenueAndDay.getEventTime();
                    LocalTime eventEndTime = eventByVenueAndDay.getEventEndTime();
                    LocalTime lectureStartTime = LocalTime.parse(startTime);
                    LocalTime lectureEndTime = LocalTime.parse(endTime);

                    boolean isLectureStartAndEndTimesEqualsToEventStartAndEndTimes =
                            lectureStartTime.equals(eventStartTime) || lectureStartTime.equals(eventEndTime)
                                    || lectureEndTime.equals(eventStartTime) || lectureEndTime.equals(eventEndTime);
                    boolean isLectureStartTimeInEventTimeDuration = lectureStartTime.isAfter(eventStartTime) && lectureStartTime.isBefore(eventEndTime);
                    boolean isLectureEndTimeInEventTimeDuration = lectureEndTime.isAfter(eventStartTime) && lectureEndTime.isBefore(eventEndTime);
                    boolean isLectureEventTimeDurationInLectureTimeDuration = lectureStartTime.isBefore(eventStartTime) && lectureEndTime.isAfter(eventEndTime);
                    if (isLectureStartAndEndTimesEqualsToEventStartAndEndTimes ||isLectureStartTimeInEventTimeDuration || isLectureEndTimeInEventTimeDuration || isLectureEventTimeDurationInLectureTimeDuration){
                        throw new RuntimeException("The Lecture On "+venue+" At "+startTime+" Is Overlapping Another Event: "+eventByVenueAndDay.getEventName());
                    }

                }


                Lecture newLecture = new Lecture();
                newLecture.setLectureDay(day);
                newLecture.setLectureName(module.get().getModuleName()+" ("+createLectureRequest.getModuleCode() +") "+ "_" + day + "_" + LocalTime.parse(startTime).format(DateTimeFormatter.ofPattern("hh:mm a")).toString());
                newLecture.setLectureAssignedUserId(createLectureRequest.getLectureAssignedUserId());
                newLecture.setLectureModuleCode(createLectureRequest.getModuleCode());
                newLecture.setLectureVenue(venue);
                newLecture.setLectureStartTime(Time.valueOf(startTime));
                newLecture.setLectureEndTime(Time.valueOf(endTime));

                Lecture savedLecture = lectureRepository.save(newLecture);
                createdLectures.add(savedLecture);
                attendanceLectureService.createLectureAttendanceTable(savedLecture.getLectureId().toString());

            }
        }

        return createdLectures;
    }

    public List<Lecture> getAllLectureByUserId(Integer userId) {
        Optional<List<Lecture>> lectures = lectureRepository.findAllByLectureAssignedUserId(userId);
        if (lectures.isPresent()) {
            return lectures.get();
        } else {
            throw new RuntimeException("There are no any Lectures found for this userID.");
        }
    }

    public List<Lecture> getAllLectureByModuleCode(String moduleCode) {
        Optional<List<Lecture>> lectures = lectureRepository.findAllByLectureModuleCode(moduleCode);
        if (lectures.isPresent()) {
            return lectures.get();
        } else {
            throw new RuntimeException("There are no any lectures found for this moduleCode");
        }

    }

    public List<Lecture> getAllLecturesByVenue(String venue) {
        Optional<List<Lecture>> lectures = lectureRepository.findAllByLectureVenue(venue);
        if (lectures.isPresent()) {
            return lectures.get();
        } else {
            throw new RuntimeException("There Are No Any Lectures Found For This Venue.");
        }
    }

    public List<Lecture> getAllLecturesByDay(String day) {
        Optional<List<Lecture>> lectures = lectureRepository.findAllByLectureDay(day);
        if (lectures.isPresent()) {
            return lectures.get();
        } else {
            throw new RuntimeException("There are no any lectures found for this day");
        }
    }

    public Lecture updateLecture(Integer lectureId, Lecture updateLecture) {
        Optional<Lecture> existingLecture = lectureRepository.findById(lectureId);
        if (existingLecture.isEmpty()) {
            throw new RuntimeException("Lecture not Found.");
        }

        Lecture lecture = existingLecture.get();
        lecture.setLectureModuleCode(updateLecture.getLectureModuleCode());
        lecture.setLectureName(updateLecture.getLectureName());
        lecture.setLectureVenue(updateLecture.getLectureVenue());
        lecture.setLectureStartTime(updateLecture.getLectureStartTime());
        lecture.setLectureEndTime(updateLecture.getLectureEndTime());
        lecture.setLectureDay(updateLecture.getLectureDay());

        Optional<List<Lecture>> lectureList = lectureRepository.findAllByLectureDayAndLectureVenue(
                updateLecture.getLectureDay(), updateLecture.getLectureVenue());
        lectureList.ifPresent(lectures -> lectures.remove(existingLecture.get()));

        for (Lecture checkLecture : lectureList.get()) {
            if (
                // Check if updateLecture starts during checkLecture
                    (updateLecture.getLectureStartTime().before(checkLecture.getLectureEndTime()) &&
                            updateLecture.getLectureStartTime().after(checkLecture.getLectureStartTime())) ||
                            updateLecture.getLectureStartTime().equals(checkLecture.getLectureStartTime()) ||
                            // Check if updateLecture ends during checkLecture
                            (updateLecture.getLectureEndTime().after(checkLecture.getLectureStartTime()) &&
                                    updateLecture.getLectureEndTime().before(checkLecture.getLectureEndTime())) ||
                            updateLecture.getLectureEndTime().equals(checkLecture.getLectureEndTime()) ||
                            // Check if updateLecture encompasses checkLecture
                            (updateLecture.getLectureStartTime().before(checkLecture.getLectureStartTime()) &&
                                    updateLecture.getLectureEndTime().after(checkLecture.getLectureEndTime()))
            ) {
                throw new RuntimeException("The updated time slot already exists");
            }
        }


        /*for (Lecture checkLecture : lectureList.get()) {
            boolean isTimeOverlapping = lectureList.isPresent() && lectureList.get().stream()
                    .anyMatch(existingLlecture -> !findTimeOverlappingLecture(updateLecture.getLectureStartTime().toString(), updateLecture.getLectureEndTime().toString(), checkLecture.getLectureId()).isEmpty());

            if (!isTimeOverlapping) {
                throw new RuntimeException("The updated time slot is already exist");
            }

        }*/

        return lectureRepository.save(lecture);

    }

    public void deleteLecture(Integer lectureId) {
        Optional<Lecture> existingLecture = lectureRepository.findById(lectureId);
        if (existingLecture.isPresent()) {
            lectureRepository.delete(existingLecture.get());
            attendanceLectureService.dropLectureAttendanceTable(lectureId);
        } else {
            throw new RuntimeException("Lecture not Found.");
        }
    }

    public List<Lecture> getAllLecturesByDayAndVenue(String day, String venue) {
        Optional<List<Lecture>> lectureList;
        if (venue.isEmpty()){
            lectureList = lectureRepository.findAllByLectureDay(day);
        }else {
            lectureList = lectureRepository.findAllByLectureDayAndLectureVenue(day,venue);
        }
        if (lectureList.isPresent()){
            return lectureList.get();
        }else {
            throw new RuntimeException("There Are No Lectures Found.");
        }
    }
}
