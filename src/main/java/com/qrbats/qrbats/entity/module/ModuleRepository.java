package com.qrbats.qrbats.entity.module;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Integer> {

    //@Query("SELECT e FROM Module e WHERE e.module.moduleCode = :moduleCode")
    Optional<Module> findByModuleCode(String moduleCode);
    Optional<List<Module>> findAllByLecturerId(Integer lectureId);

    Optional<List<Module>> findAllBySemesterAndDepartmentId(Integer semester, Integer departmentId);

    Optional<List<Module>> findAllByDepartmentId(Integer departmentId);

}
