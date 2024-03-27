package com.qrbats.qrbats.functionalities.module_creation.services.impl;

import com.qrbats.qrbats.entity.module.Module;
import com.qrbats.qrbats.entity.module.ModuleRepository;
import com.qrbats.qrbats.functionalities.module_creation.dto.ModuleCreationRequest;
import com.qrbats.qrbats.functionalities.module_creation.dto.ModuleDeletionRequest;
import com.qrbats.qrbats.functionalities.module_creation.dto.ModuleUpdateRequest;
import com.qrbats.qrbats.functionalities.module_creation.services.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ModuleServiceImpl implements ModuleService {

    private final ModuleRepository moduleRepository;
    @Override
    public Module createModule(ModuleCreationRequest moduleCreationRequest) {

        Optional<Module> oldModule = moduleRepository.findByModuleCode(moduleCreationRequest.getModuleCode());
        Module module;
        module = oldModule.orElseGet(Module::new);
        module.setModuleCode(moduleCreationRequest.getModuleCode());
        module.setModuleName(moduleCreationRequest.getModuleName());
        module.setModuleEnrolmentKey(moduleCreationRequest.getModuleEnrolmentKey());
        module.setSemester(moduleCreationRequest.getSemester());
        module.setLecturerId(moduleCreationRequest.getLectureId());
        module.setDepartmentId(moduleCreationRequest.getDepartmentId());
        return moduleRepository.save(module);

    }


    @Override
    public void deleteModule(ModuleDeletionRequest moduleDeletionRequest){
        String deleteModuleCode = moduleDeletionRequest.getModuleCode();
        Optional<Module> deleteModule = moduleRepository.findByModuleCode(deleteModuleCode);
        if(deleteModule.isPresent()){
            moduleRepository.delete(deleteModule.get());
        }
        else {
            throw new RuntimeException("module not found.");
        }
    }

    @Override
    public void updateModule(ModuleUpdateRequest moduleUpdateRequest) {
        Optional<Module> oldModule = moduleRepository.findByModuleCode(moduleUpdateRequest.getOldModuleCode());
        if(oldModule.isPresent()){
            oldModule.get().setModuleCode(moduleUpdateRequest.getModuleCode());
            oldModule.get().setModuleName(moduleUpdateRequest.getModuleName());
            oldModule.get().setModuleEnrolmentKey(moduleUpdateRequest.getModuleEnrolmentKey());
            oldModule.get().setSemester(moduleUpdateRequest.getSemester());
            oldModule.get().setDepartmentId(moduleUpdateRequest.getDepartmentId());
            oldModule.get().setLecturerId(moduleUpdateRequest.getLectureId());

            moduleRepository.save(oldModule.get());
        }else {
            throw new RuntimeException("Module Not Found");
        }
    }

    @Override
    public List<Module> getModuleByLecturerId(Integer lecturerId) {
        Optional<List<Module>> lecturerModulesList = moduleRepository.findAllByLecturerId(lecturerId);
        if(lecturerModulesList.isPresent()){
            return lecturerModulesList.get();
        }else {
            throw new RuntimeException("Module not found.");
        }
    }

    @Override
    public List<Module> getModuleBySemesterDepartment(Integer semesterId, Integer departmentId) {
        Optional<List<Module>> moduleList = moduleRepository.findAllBySemesterAndDepartmentId(semesterId,departmentId);
        if (moduleList.isPresent()){
            return moduleList.get();
        }else {
            throw new RuntimeException("Module not found");
        }
    }


}
