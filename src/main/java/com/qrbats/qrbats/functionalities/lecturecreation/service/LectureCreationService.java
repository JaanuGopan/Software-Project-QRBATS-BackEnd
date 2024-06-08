package com.qrbats.qrbats.functionalities.lecturecreation.service;

import com.qrbats.qrbats.entity.lecture.Lecture;
import com.qrbats.qrbats.functionalities.lecturecreation.dto.CreateLectureRequest;

import java.util.List;

public interface LectureCreationService {
    List<Lecture> createLecture(CreateLectureRequest createLectureRequest);

    List<Lecture> getAllLectureByUserId(Integer userId);
    List<Lecture> getAllLectureByModuleCode(String moduleCode);

    List<Lecture> getAllLecturesByVenue(String venue);
    List<Lecture> getAllLecturesByDay(String day);

    Lecture updateLecture(Integer lectureId, Lecture updateLecture);

    void deleteLecture(Integer lectureId);

    List<Lecture> getAllLecturesByDayAndVenue(String day,String venue);
}
