package com.qrbats.qrbats.entity.module;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ModuleRepository extends JpaRepository<Module,Integer> {
    Optional<Module> findByModuleCode(String moduleCode);
    Optional<List<Module>> findAllByCreatedBy(Integer userId);

    Optional<List<Module>> findAllBySemesterAndDepartmentId(Integer semester, Integer departmentId);
    Optional<List<Module>> findAllBySemesterAndDepartmentIdAndStudentBatch(
            Integer semester,
            Integer departmentId,
            Integer studentBatch
    );

    Optional<List<Module>> findAllByDepartmentId(Integer departmentId);

    Optional<Module> findByModuleName(String moduleName);
}
