package com.qrbats.qrbats.entity.attendance;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class AttendanceLectureService {
    @Autowired
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void createLectureAttendanceTable(String lectureId) {
        String tableName = "Attendance_" + lectureId;
        String createTableQuery = "CREATE TABLE " + tableName + " (" +
                "Attendance_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "Event_id INT, " +
                "Attendee_id INT, " +
                "Attendance_date DATE, " +
                "Attendance_time TIME, " +
                "Attendance_status BOOL)";
        entityManager.createNativeQuery(createTableQuery).executeUpdate();
    }

    @Transactional
    public void saveAttendance(String lectureId, Attendance attendance) {
        String tableName = "Attendance_" + lectureId;
        String insertQuery = "INSERT INTO " + tableName + " (Event_id, Attendee_id, Attendance_date, Attendance_time, Attendance_status) VALUES (?, ?, ?, ?, ?)";
        entityManager.createNativeQuery(insertQuery)
                .setParameter(1, attendance.getEventId())
                .setParameter(2, attendance.getAttendeeId())
                .setParameter(3, attendance.getAttendanceDate())
                .setParameter(4, attendance.getAttendanceTime())
                .setParameter(5, true)
                .executeUpdate();
    }

    @Transactional
    public void deleteAttendance(String lectureId, Integer attendanceId) {
        String tableName = "Attendance_" + lectureId;
        String deleteQuery = "DELETE FROM " + tableName + " WHERE Attendance_id = ?";
        entityManager.createNativeQuery(deleteQuery)
                .setParameter(1, attendanceId)
                .executeUpdate();
    }
    @Transactional
    public void dropLectureAttendanceTable(String lectureId) {
        String tableName = "Attendance_" + lectureId;
        String dropTableQuery = "DROP TABLE " + tableName;
        entityManager.createNativeQuery(dropTableQuery).executeUpdate();
    }

    @Transactional
    public List<AttendanceLecture> getAllAttendanceByLectureId(String lectureId){
        String tableName = "Attendance_" + lectureId;
        String selectQuery = "SELECT * FROM " + tableName;
        Query query = entityManager.createNativeQuery(selectQuery, AttendanceLecture.class);
        return query.getResultList();
    }
}
