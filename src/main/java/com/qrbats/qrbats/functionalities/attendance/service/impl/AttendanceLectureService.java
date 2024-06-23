package com.qrbats.qrbats.functionalities.attendance.service.impl;

import com.qrbats.qrbats.authentication.entities.student.Student;
import com.qrbats.qrbats.entity.attendance.AttendanceEvent;
import com.qrbats.qrbats.entity.attendance.AttendanceLecture;
import com.qrbats.qrbats.entity.lecture.Lecture;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AttendanceLectureService {
    @Autowired
    @PersistenceContext
    private EntityManager entityManager;


    @Transactional
    public void createLectureAttendanceTable(String lectureId) {
        String tableName = "Attendance_lec_" + lectureId;
        String createTableQuery = "CREATE TABLE " + tableName + " (" +
                "Attendance_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "Lecture_id INT, " +
                "Attendee_id INT, " +
                "Attendance_date DATE, " +
                "Attendance_time TIME, " +
                "Attendance_status BOOL)";
        try {
            entityManager.createNativeQuery(createTableQuery).executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Transactional
    public void saveLectureAttendance(String lectureId, AttendanceEvent attendanceEvent) {
        String tableName = "Attendance_lec_" + lectureId;
        String insertQuery = "INSERT INTO " + tableName + " (Lecture_id, Attendee_id, Attendance_date, Attendance_time, Attendance_status) VALUES (?, ?, ?, ?, ?)";
        try {
            entityManager.createNativeQuery(insertQuery)
                    .setParameter(1, attendanceEvent.getEventId())
                    .setParameter(2, attendanceEvent.getAttendeeId())
                    .setParameter(3, attendanceEvent.getAttendanceDate())
                    .setParameter(4, attendanceEvent.getAttendanceTime())
                    .setParameter(5, true)
                    .executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error In Saving Attendance. ",e);
        }
    }
    @Transactional
    public List<AttendanceLecture> getAttendanceByLectureIdAndStudentIdAndDate(Integer lectureId, Integer studentId, Date date){
        String tableName = "Attendance_lec_" + lectureId.toString();
        String selectQuery = "SELECT * FROM " + tableName + " WHERE Lecture_id="+lectureId.toString()
                +" AND Attendee_id="+studentId.toString()+" And Attendance_date="+"'"+date+"'";
        try {
            Query query = entityManager.createNativeQuery(selectQuery, AttendanceLecture.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error In Getting Attendance. ",e);
        }
    }
    @Transactional
    public List<AttendanceLecture> getAttendanceByLectureIdAndStudentId(Integer lectureId, Integer studentId){
        String tableName = "Attendance_lec_" + lectureId.toString();
        String selectQuery = "SELECT * FROM " + tableName + " WHERE Lecture_id="+lectureId.toString()
                +" AND Attendee_id="+studentId.toString();
        try {
            Query query = entityManager.createNativeQuery(selectQuery, AttendanceLecture.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error In Getting Attendance. ",e);
        }
    }
    @Transactional
    public void deleteLectureAttendance(String lectureId, Integer attendanceId) {
        String tableName = "Attendance_lec_" + lectureId;
        String deleteQuery = "DELETE FROM " + tableName + " WHERE Attendance_id = ?";
        entityManager.createNativeQuery(deleteQuery)
                .setParameter(1, attendanceId)
                .executeUpdate();
    }
    @Transactional
    public void dropLectureAttendanceTable(Integer lectureId) {
        String tableName = "Attendance_lec_" + lectureId.toString();
        String dropTableQuery = "DROP TABLE IF EXISTS " + tableName;
        try {
            entityManager.createNativeQuery(dropTableQuery).executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error dropping table " + tableName, e);
        }
    }

    @Transactional
    public List<AttendanceLecture> getAllAttendanceByLectureId(Integer lectureId){
        String tableName = "Attendance_lec_" + lectureId.toString();
        String selectQuery = "SELECT * FROM " + tableName;
        try {
            Query query = entityManager.createNativeQuery(selectQuery, AttendanceLecture.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error In Getting All Attendance For This Module. ",e);
        }
    }

    @Transactional
    public Integer getLectureCountByLectureId(Integer lectureId) {
        String tableName = "Attendance_lec_" + lectureId.toString();
        String selectQuery = "SELECT COUNT(DISTINCT Attendance_date) AS lecture_count FROM " + tableName;
        try {
            Query query = entityManager.createNativeQuery(selectQuery);
            Number result = (Number) query.getSingleResult();
            return result.intValue();
        } catch (Exception e) {
            throw new RuntimeException("Error Getting Lecture Count.", e);
        }
    }

    @Transactional
    public List<Date> getLecturesByLectureId(Integer lectureId) {
        String tableName = "Attendance_lec_" + lectureId.toString();
        String selectQuery = "SELECT DISTINCT Attendance_date FROM " + tableName;
        try {
            Query query = entityManager.createNativeQuery(selectQuery);
            return (List<Date>) query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error In Getting Date List.", e);
        }
    }


    @Transactional
    public List<AttendanceLecture> getAttendanceByLectureIdAndDate(Integer lectureId, Date date){
        String tableName = "Attendance_lec_" + lectureId.toString();
        String selectQuery = "SELECT * FROM " + tableName + " WHERE Lecture_id="+lectureId.toString()
                +" And Attendance_date="+"'"+date+"'";
        try {
            Query query = entityManager.createNativeQuery(selectQuery, AttendanceLecture.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error In Getting Attendance. ",e);
        }
    }








}
