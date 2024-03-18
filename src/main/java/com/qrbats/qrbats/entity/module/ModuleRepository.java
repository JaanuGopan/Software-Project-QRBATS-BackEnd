package com.qrbats.qrbats.entity.module;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Integer> {

    //@Query("SELECT e FROM Module e WHERE e.module.moduleCode = :moduleCode")
    List<Module> findByModuleCode(String moduleCode);

}
