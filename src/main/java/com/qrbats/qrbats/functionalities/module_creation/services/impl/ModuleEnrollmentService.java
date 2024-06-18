package com.qrbats.qrbats.functionalities.module_creation.services.impl;

import com.qrbats.qrbats.entity.moduleenrolment.ModuleEnrolment;
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
public class ModuleEnrollmentService {
    @Autowired
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void createModuleEnrollmentTable(Integer moduleId) {
        String tableName = "ModuleEnrollment_" + moduleId;
        String createTableQuery = "CREATE TABLE " + tableName + " (" +
                "ModuleEnrollment_Id INT AUTO_INCREMENT PRIMARY KEY, " +
                "Module_Id INT, " +
                "Student_Id INT )";
        try {
            entityManager.createNativeQuery(createTableQuery).executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void deleteModuleEnrollmentTable(Integer moduleId){
        String tableName = "ModuleEnrollment_" + moduleId;
        String dropTableQuery = "DROP TABLE IF EXISTS " + tableName;
        try {
            entityManager.createNativeQuery(dropTableQuery).executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error Dropping Table " + tableName, e);
        }
    }

    @Transactional
    public boolean studentModuleEnrollment(Integer moduleId,Integer studentId){
        String tableName = "ModuleEnrollment_" + moduleId;
        String insertQuery = "INSERT INTO " + tableName + " (Module_Id, Student_Id) VALUES (?, ?)";
        try {
            boolean studentEnrollmentStatus = checkStudentEnrollment(moduleId,studentId);
            if (!studentEnrollmentStatus) {
                entityManager.createNativeQuery(insertQuery)
                        .setParameter(1, moduleId)
                        .setParameter(2, studentId)
                        .executeUpdate();
                return true;
            }else {
                throw new RuntimeException("Student Already Enrolled This Module.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error In Enrolling Module. ",e);
        }
    }

    @Transactional
    public boolean checkStudentEnrollment(Integer moduleId, Integer studentId){
        String tableName = "ModuleEnrollment_" + moduleId;
        String selectQuery = "Select * FROM " + tableName + " WHERE Module_Id = ? And Student_Id = ?";
        try {
            Query query = entityManager.createNativeQuery(selectQuery, ModuleEnrolment.class)
                    .setParameter(1, moduleId)
                    .setParameter(2, studentId);
            List<ModuleEnrolment> moduleEnrolments = query.getResultList();
            if (moduleEnrolments.isEmpty()){
                return false;
            }else {
                return true;
            }
        }catch (Exception e){
            throw new RuntimeException("Error In Checking Enrolment Status.");
        }
    }

    @Transactional
    public boolean moduleUnEnrollment(Integer moduleId, Integer studentId){
        String tableName = "ModuleEnrollment_" + moduleId;
        String deleteQuery = "DELETE FROM " + tableName + " WHERE Student_Id = ?";
        try {
            entityManager.createNativeQuery(deleteQuery)
                    .setParameter(1, studentId)
                    .executeUpdate();
            return true;
        } catch (Exception e){
            return false;
        }
    }

    @Transactional
    public List<ModuleEnrolment> getModuleEnrolmentListByModuleId(Integer moduleId){
        String tableName = "ModuleEnrollment_" + moduleId;
        String selectQuery = "Select * FROM " + tableName;

        try {
            Query query = entityManager.createNativeQuery(selectQuery, ModuleEnrolment.class);
            List<ModuleEnrolment> moduleEnrolments = query.getResultList();
            return moduleEnrolments;
        }catch (Exception e){
            throw new RuntimeException("Error In Getting Enrolled Students.");
        }
    }
}
