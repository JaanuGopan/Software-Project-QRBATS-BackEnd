package com.qrbats.qrbats.functionalities.lecturecreation.service.impl;

import com.qrbats.qrbats.authentication.entities.user.User;
import com.qrbats.qrbats.authentication.entities.user.repository.UserRepository;
import com.qrbats.qrbats.functionalities.attendance.service.impl.AttendanceLectureService;
import com.qrbats.qrbats.entity.lecture.Lecture;
import com.qrbats.qrbats.entity.lecture.LectureRepository;
import com.qrbats.qrbats.entity.location.LocationRepository;
import com.qrbats.qrbats.entity.module.Module;
import com.qrbats.qrbats.entity.module.ModuleRepository;
import com.qrbats.qrbats.functionalities.lecturecreation.dto.CreateLectureRequest;
import com.qrbats.qrbats.functionalities.lecturecreation.dto.TimeSlot;
import com.qrbats.qrbats.functionalities.lecturecreation.service.LectureCreationService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LectureCreationServiceImpl implements LectureCreationService {
    @Autowired
    private final LectureRepository lectureRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final ModuleRepository moduleRepository;
    @Autowired
    private final LocationRepository locationRepository;
    @Autowired
    private final AttendanceLectureService attendanceLectureService;

    @Autowired
    @PersistenceContext
    private EntityManager entityManager;

    @Override
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

                Optional<List<Lecture>> existingLectures = lectureRepository.findAllByLectureDayAndLectureVenue(day, venue);
                boolean isTimeOverlapping = existingLectures.isPresent() && existingLectures.get().stream()
                        .anyMatch(existingLecture -> !findTimeOverlappingLecture(startTime, endTime, existingLecture.getLectureId()).isEmpty());

                if (!isTimeOverlapping) {
                    Lecture newLecture = new Lecture();
                    newLecture.setLectureDay(day);
                    newLecture.setLectureName(createLectureRequest.getModuleCode() + "_" + day + "_" + startTime + "_Lecture");
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
        }

        return createdLectures;
    }

    @Override
    public List<Lecture> getAllLectureByUserId(Integer userId) {
        Optional<List<Lecture>> lectures = lectureRepository.findAllByLectureAssignedUserId(userId);
        if (lectures.isPresent()) {
            return lectures.get();
        } else {
            throw new RuntimeException("There are no any Lectures found for this userID.");
        }
    }

    @Override
    public List<Lecture> getAllLectureByModuleCode(String moduleCode) {
        Optional<List<Lecture>> lectures = lectureRepository.findAllByLectureModuleCode(moduleCode);
        if (lectures.isPresent()) {
            return lectures.get();
        } else {
            throw new RuntimeException("There are no any lectures found for this moduleCode");
        }

    }

    @Override
    public List<Lecture> getAllLecturesByVenue(String venue) {
        Optional<List<Lecture>> lectures = lectureRepository.findAllByLectureVenue(venue);
        if (lectures.isPresent()) {
            return lectures.get();
        } else {
            throw new RuntimeException("There Are No Any Lectures Found For This Venue.");
        }
    }

    @Override
    public List<Lecture> getAllLecturesByDay(String day) {
        Optional<List<Lecture>> lectures = lectureRepository.findAllByLectureDay(day);
        if (lectures.isPresent()) {
            return lectures.get();
        } else {
            throw new RuntimeException("There are no any lectures found for this day");
        }
    }


    @Transactional
    public List<Lecture> findTimeOverlappingLecture(String startTime, String endTime, Integer lectureId) {
        String query = "SELECT * FROM Lecture WHERE " +
                "(lecture_id = ?) AND " +
                "((lecture_start_time <= ? AND lecture_end_time > ?) " +
                "OR (lecture_start_time < ? AND lecture_end_time >= ?))";

        Query resultQuery = entityManager.createNativeQuery(query, Lecture.class)
                .setParameter(1, lectureId)
                .setParameter(2, startTime)
                .setParameter(3, startTime)
                .setParameter(4, endTime)
                .setParameter(5, endTime);

        return resultQuery.getResultList();
    }

    @Override
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
        if (lectureList.isPresent()) lectureList.get().remove(existingLecture.get());

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

    @Override
    public void deleteLecture(Integer lectureId) {
        Optional<Lecture> existingLecture = lectureRepository.findById(lectureId);
        if (existingLecture.isPresent()) {
            lectureRepository.delete(existingLecture.get());
            attendanceLectureService.dropLectureAttendanceTable(lectureId);
        } else {
            throw new RuntimeException("Lecture not Found.");
        }
    }

    @Override
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
