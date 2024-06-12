package com.qrbats.qrbats.functionalities.module_creation.services.impl;

import com.qrbats.qrbats.authentication.entities.student.Student;
import com.qrbats.qrbats.authentication.entities.student.repository.StudentRepository;
import com.qrbats.qrbats.entity.module.Module;
import com.qrbats.qrbats.entity.module.ModuleRepository;
import com.qrbats.qrbats.functionalities.module_creation.dto.ModuleCreationRequest;
import com.qrbats.qrbats.functionalities.module_creation.dto.ModuleDeletionRequest;
import com.qrbats.qrbats.functionalities.module_creation.dto.ModuleUpdateRequest;
import com.qrbats.qrbats.functionalities.module_creation.services.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ModuleServiceImpl implements ModuleService {

    @Autowired
    private final ModuleRepository moduleRepository;
    @Autowired
    private final StudentRepository studentRepository;

    @Override
    public Module createModule(ModuleCreationRequest moduleCreationRequest) {

        Optional<Module> oldModule = moduleRepository.findByModuleCode(moduleCreationRequest.getModuleCode());
        Module module;
        module = oldModule.orElseGet(Module::new);
        module.setModuleCode(moduleCreationRequest.getModuleCode());
        module.setModuleName(moduleCreationRequest.getModuleName());
        module.setModuleEnrolmentKey(moduleCreationRequest.getModuleEnrolmentKey());
        module.setSemester(moduleCreationRequest.getSemester());
        module.setLecturerId(moduleCreationRequest.getUserId());
        module.setDepartmentId(moduleCreationRequest.getDepartmentId());
        return moduleRepository.save(module);

    }


    @Override
    public void deleteModule(Integer moduleId) {
        Integer deleteModuleId = moduleId;
        Optional<Module> deleteModule = moduleRepository.findById(deleteModuleId);
        if (deleteModule.isPresent()) {
            moduleRepository.delete(deleteModule.get());
        } else {
            throw new RuntimeException("module not found for delete.");
        }
    }

    @Override
    public void updateModule(ModuleUpdateRequest moduleUpdateRequest) {
        Optional<Module> oldModule = moduleRepository.findByModuleCode(moduleUpdateRequest.getOldModuleCode());
        if (oldModule.isPresent()) {
            oldModule.get().setModuleCode(moduleUpdateRequest.getModuleCode());
            oldModule.get().setModuleName(moduleUpdateRequest.getModuleName());
            oldModule.get().setModuleEnrolmentKey(moduleUpdateRequest.getModuleEnrolmentKey());
            oldModule.get().setSemester(moduleUpdateRequest.getSemester());
            oldModule.get().setDepartmentId(moduleUpdateRequest.getDepartmentId());
            oldModule.get().setLecturerId(moduleUpdateRequest.getLectureId());

            moduleRepository.save(oldModule.get());
        } else {
            throw new RuntimeException("Module Not Found");
        }
    }

    @Override
    public List<Module> getModuleByLecturerId(Integer lecturerId) {
        Optional<List<Module>> lecturerModulesList = moduleRepository.findAllByLecturerId(lecturerId);
        if (lecturerModulesList.isPresent()) {
            return lecturerModulesList.get();
        } else {
            throw new RuntimeException("Module not found.");
        }
    }

    @Override
    public List<Module> getModuleBySemesterDepartment(Integer semesterId, Integer departmentId) {
        Optional<List<Module>> moduleList = moduleRepository.findAllBySemesterAndDepartmentId(semesterId, departmentId);
        if (moduleList.isPresent()) {
            return moduleList.get();
        } else {
            throw new RuntimeException("Module not found");
        }
    }

    @Override
    public List<Module> getModuleByDepartmentId(Integer departmentId) {
        Optional<List<Module>> modules = moduleRepository.findAllByDepartmentId(departmentId);
        if (modules.isPresent()) {

            return modules.get();
        } else {
            throw new RuntimeException("Modules Not Found.");
        }
    }

    @Override
    public List<Module> getAllModulesByStudentId(Integer studentId) {
        Optional<Student> student = studentRepository.findById(studentId);
        if (!student.isPresent()) throw new RuntimeException("Student Not Found.");
        Optional<List<Module>> moduleList = moduleRepository.findAllBySemesterAndDepartmentId(student.get().getCurrentSemester(),student.get().getDepartmentId());
        if (!moduleList.isPresent()) throw new RuntimeException("Modules Not Found.");

        return moduleList.get();
    }


}
