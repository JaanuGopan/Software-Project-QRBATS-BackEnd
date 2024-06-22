package com.qrbats.qrbats.functionalities.attendance.service.impl;

import com.qrbats.qrbats.entity.attendance.AttendanceEvent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class AttendanceEventService {
    @Autowired
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void createEventAttendanceTable(Integer EventId) {
        String tableName = "Attendance_eve_" + EventId.toString();
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
    public void saveEventAttendance(Integer eventId, AttendanceEvent attendanceEvent) {
        String tableName = "Attendance_eve_" + eventId.toString();
        String insertQuery = "INSERT INTO " + tableName +
                " (Event_id, Attendee_id, Attendance_date, Attendance_time, Attendance_status) VALUES (?, ?, ?, ?, ?)";
        try {
            entityManager.createNativeQuery(insertQuery)
                    .setParameter(1, attendanceEvent.getEventId())
                    .setParameter(2, attendanceEvent.getAttendeeId())
                    .setParameter(3, attendanceEvent.getAttendanceDate())
                    .setParameter(4, attendanceEvent.getAttendanceTime())
                    .setParameter(5, true)
                    .executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Event Attendance Saved Failed. ",e);
        }
    }
    @Transactional
    public AttendanceEvent getAttendanceByEventIdAndStudentId(Integer eventId, Integer studentId){
        String tableName = "Attendance_eve_" + eventId.toString();
        String selectQuery = "SELECT * FROM " + tableName + " WHERE Attendee_id = " + studentId;
        try{
            Query query = entityManager.createNativeQuery(selectQuery, AttendanceEvent.class);
            AttendanceEvent singleResult = (AttendanceEvent) query.getSingleResult();
            return singleResult;
        } catch (Exception ex){
            throw new RuntimeException("Failed To Find Attendance For This Event ",ex);
        }
    }

    @Transactional
    public AttendanceEvent getStudentAttendanceByEventIdAndStudentId(Integer eventId, Integer studentId){
        String tableName = "Attendance_eve_" + eventId.toString();
        String selectQuery = "SELECT * FROM " + tableName + " WHERE Attendee_id = " + studentId;
        try{
            Query query = entityManager.createNativeQuery(selectQuery, AttendanceEvent.class);
            AttendanceEvent singleResult = (AttendanceEvent) query.getSingleResult();
            return singleResult;
        } catch (Exception ex){
            return null;
        }
    }
    @Transactional
    public List<AttendanceEvent> getAttendanceByEventIdAndStudentIdAndDate(Integer eventId, Integer studentId, Date date){
        String tableName = "Attendance_eve_" + eventId.toString();
        String selectQuery = "SELECT * FROM " + tableName + " WHERE Event_id="+eventId.toString()
                +" AND Attendee_id="+studentId.toString()+" And Attendance_date="+"'"+date+"'";
        Query query = entityManager.createNativeQuery(selectQuery, AttendanceEvent.class);
        return query.getResultList();
    }
    @Transactional
    public void deleteEventOneAttendance(Integer eventId, Integer attendanceId) {
        String tableName = "Attendance_eve_" + eventId.toString();
        String deleteQuery = "DELETE FROM " + tableName + " WHERE Attendance_id = ?";
        entityManager.createNativeQuery(deleteQuery)
                .setParameter(1, attendanceId)
                .executeUpdate();
    }
    @Transactional
    public void dropEventAttendanceTable(Integer eventId) {
        String tableName = "Attendance_eve_" + eventId;
        String dropTableQuery = "DROP TABLE IF EXISTS " + tableName;
        try {
            entityManager.createNativeQuery(dropTableQuery).executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error dropping table " + tableName, e);
        }
    }

    @Transactional
    public List<AttendanceEvent> getAllAttendanceByEventId(Integer eventId){
        String tableName = "Attendance_eve_" + eventId.toString();
        String selectQuery = "SELECT * FROM " + tableName;
        try {
            Query query = entityManager.createNativeQuery(selectQuery, AttendanceEvent.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error In Get All Attendance.",e);
        }
    }

}
