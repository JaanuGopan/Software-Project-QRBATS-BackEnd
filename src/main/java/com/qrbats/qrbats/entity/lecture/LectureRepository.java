package com.qrbats.qrbats.entity.lecture;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LectureRepository extends JpaRepository<Lecture,Integer> {
    Optional<List<Lecture>> findAllByLectureModuleCode(String moduleCode);
    Optional<List<Lecture>> findAllByLectureModuleCodeAndLectureDay(String moduleCode,String day);
    Optional<List<Lecture>> findAllByLectureAssignedUserId(Integer userId);
    Optional<List<Lecture>> findAllByLectureDay(String day);
    Optional<List<Lecture>> findAllByLectureVenue(String venue);
    Optional<List<Lecture>> findAllByLectureDayAndLectureVenue(String day,String venue);
    Optional<List<Lecture>> findAllByLectureDayAndLectureVenueAndLectureModuleCode(
            String day,String venue, String moduleCode);


}
