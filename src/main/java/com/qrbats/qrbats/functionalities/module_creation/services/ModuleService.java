package com.qrbats.qrbats.functionalities.module_creation.services;

import com.qrbats.qrbats.authentication.entities.student.Student;
import com.qrbats.qrbats.entity.module.Module;
import com.qrbats.qrbats.functionalities.module_creation.dto.ModuleCreationRequest;
import com.qrbats.qrbats.functionalities.module_creation.dto.ModuleDeletionRequest;
import com.qrbats.qrbats.functionalities.module_creation.dto.ModuleUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface ModuleService {

    Module getModuleByModuleCode(String moduleCode);
    Module createModule(ModuleCreationRequest moduleCreationResponse);
    void deleteModule(Integer  moduleCode);
    Module updateModule(ModuleUpdateRequest moduleUpdateRequest);
    List<Module> getModuleByLecturerId(Integer lecturerId);

    List<Module> getModuleBySemesterDepartment(Integer semesterId,Integer departmentId);

    List<Module> getModuleByDepartmentId(Integer departmentId);
    List<Module> getAllModulesByStudentId(Integer studentId);

    boolean moduleEnrollment(Integer moduleId, Integer studentId, String enrolmentKey);

    List<Module> getAllEnrolledModules(Integer studentId);

    List<Student> getAllEnrolledStudentByModuleCode(String moduleCode);
}
