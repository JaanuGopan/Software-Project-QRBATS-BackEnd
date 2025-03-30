package com.qrbats.qrbats.entity.module_enrollment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleEnrollmentRepository extends JpaRepository<ModuleEnrollment, Long> {
    Optional<List<ModuleEnrollment>> findAllByModuleId(Integer moduleId);
    Optional<List<ModuleEnrollment>> findAllByStudentId(Integer studentId);
    Optional<ModuleEnrollment> findByStudentIdAndModuleId(Integer studentId, Integer moduleId);
    void deleteAllByModuleId(Integer moduleId);
}
