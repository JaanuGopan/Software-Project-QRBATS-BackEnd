package com.qrbats.qrbats.functionalities.module_creation.services;

import com.qrbats.qrbats.entity.module.Module;
import com.qrbats.qrbats.functionalities.module_creation.dto.ModuleCreationRequest;
import com.qrbats.qrbats.functionalities.module_creation.dto.ModuleDeletionRequest;
import com.qrbats.qrbats.functionalities.module_creation.dto.ModuleUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface ModuleService {

    Module createModule(ModuleCreationRequest moduleCreationResponse);
    void deleteModule(Integer  moduleCode);
    void updateModule(ModuleUpdateRequest moduleUpdateRequest);
    List<Module> getModuleByLecturerId(Integer lecturerId);

    List<Module> getModuleBySemesterDepartment(Integer semesterId,Integer departmentId);

    List<Module> getModuleByDepartmentId(Integer departmentId);
    List<Module> getAllModulesByStudentId(Integer studentId);
}
