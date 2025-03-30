package com.qrbats.qrbats.functionalities.module.service;

import com.qrbats.qrbats.authentication.entities.user.User;
import com.qrbats.qrbats.authentication.services.UserService;
import com.qrbats.qrbats.config.AuditInfoProvider;
import com.qrbats.qrbats.entity.lecture.LectureRepository;
import com.qrbats.qrbats.entity.module.Module;
import com.qrbats.qrbats.entity.module.ModuleRepository;
import com.qrbats.qrbats.entity.module_enrollment.ModuleEnrollment;
import com.qrbats.qrbats.functionalities.module.dto.ModuleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final ModuleEnrollmentService moduleEnrollmentService;
    private final AuditInfoProvider auditInfoProvider;
    private final UserService userService;
    private final LectureRepository lectureRepository;

    public Module getModuleByModuleId(Integer moduleId) {
        return moduleRepository.findById(moduleId)
                .orElseThrow(() -> new IllegalArgumentException("Module not found for this moduleId - " + moduleId));
    }

    public Module getModuleByModuleCode(String moduleCode) {
        Optional<Module> module = moduleRepository.findByModuleCode(moduleCode);
        if (module.isEmpty()) throw new IllegalArgumentException("There Is No Module For This ModuleCode "+ moduleCode);
        return module.get();
    }

    public Module getModuleByModuleIdForStudent(Integer moduleId) {
        Optional<Module> module = moduleRepository.findById(moduleId);
        if (module.isEmpty()) throw new IllegalArgumentException("Module not found.");
        ModuleEnrollment moduleEnrollment = moduleEnrollmentService.getModuleEnrollmentByStudentIdAndModuleId(auditInfoProvider.getCurrentUserIdentifier(), moduleId);

        return module.get();
    }

    public Boolean checkStudentModuleEnrollment(Integer moduleId) {
        ModuleEnrollment moduleEnrollment = moduleEnrollmentService
                .getModuleEnrollmentByStudentIdAndModuleId(auditInfoProvider.getCurrentUserIdentifier(), moduleId);
        return moduleEnrollment != null;
    }

    public Module saveModule(ModuleDto moduleDto) {
        
        if (moduleDto.getStatingDate().isAfter(moduleDto.getEndingDate())){
            throw new IllegalArgumentException("The module start time should be before module end time.");
        }
        
        if (moduleDto.getId() == null) {
            if (moduleDto.getStatingDate().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("The module start time should be in future.");
            }

            Optional<List<Module>> moduleList = moduleRepository.findAllByModuleCode(moduleDto.getModuleCode());
            if (moduleList.isPresent()){
                for (Module module : moduleList.get()) {
                    if (module.getStudentBatch().equals(moduleDto.getStudentBatch())){
                        throw new IllegalArgumentException("This Module is already created for " + moduleDto.getStudentBatch());
                    }
                }
            }
            
            Module module = new Module();
            module.setModuleCode(moduleDto.getModuleCode());
            module.setModuleName(moduleDto.getModuleName());
            module.setModuleEnrolmentKey(moduleDto.getModuleEnrolmentKey());
            module.setSemester(moduleDto.getSemester());
            module.setStudentBatch(moduleDto.getStudentBatch());
            module.setDepartmentId(moduleDto.getDepartmentId());
            module.setCreatedBy(auditInfoProvider.getCurrentUserIdentifier());
            module.setStatingDate(moduleDto.getStatingDate());
            module.setEndingDate(moduleDto.getEndingDate());
            
            return moduleRepository.save(module);
        } else {
            Module module = getModuleByModuleId(moduleDto.getId());
            if (moduleDto.getModuleCode() != null) module.setModuleCode(moduleDto.getModuleCode());
            if (moduleDto.getModuleName() != null) module.setModuleName(moduleDto.getModuleName());
            if (moduleDto.getModuleEnrolmentKey() != null) module.setModuleEnrolmentKey(moduleDto.getModuleEnrolmentKey());
            if (moduleDto.getSemester() != null) module.setSemester(moduleDto.getSemester());
            if (moduleDto.getStudentBatch() != null) module.setStudentBatch(moduleDto.getStudentBatch());
            if (moduleDto.getDepartmentId() != null) module.setDepartmentId(moduleDto.getDepartmentId());
            if (moduleDto.getStatingDate() != null) module.setStatingDate(moduleDto.getStatingDate());
            if (moduleDto.getEndingDate() != null) module.setEndingDate(moduleDto.getEndingDate());

            return moduleRepository.save(module);
        }
    }

    public void deleteModule(Integer moduleId) {
        Module module = getModuleByModuleId(moduleId);
        if (!module.getCreatedBy().equals(auditInfoProvider.getCurrentUserIdentifier())){
            throw new IllegalArgumentException("You are not authorized to delete this module.");
        }
        moduleRepository.delete(module);
        lectureRepository.deleteAllByModuleId(moduleId);
        moduleEnrollmentService.deleteAllEnrollmentByModuleId(moduleId);
    }


    public List<Module> getModuleByLecturerId() {
        return moduleRepository.findAllByCreatedBy(auditInfoProvider.getCurrentUserIdentifier())
                .orElseThrow(() -> new IllegalArgumentException("Module not found."));
    }

    public List<Module> getModuleBySemesterDepartment(Integer semesterId, Integer departmentId) {
        return moduleRepository.findAllBySemesterAndDepartmentId(semesterId, departmentId)
                .orElseThrow(() -> new IllegalArgumentException("Module not found."));
    }

    public List<Module> getModuleBySemesterAndDepartmentAndBatch(Integer semesterId, Integer departmentId, Integer studentBatch) {
        return moduleRepository
                .findAllBySemesterAndDepartmentIdAndStudentBatch(semesterId, departmentId, studentBatch)
                .orElseThrow(() -> new IllegalArgumentException("Module not found."));
    }

    public List<Module> getModuleByDepartmentId(Integer departmentId) {
        Optional<List<Module>> modules = moduleRepository.findAllByDepartmentId(departmentId);
        if (modules.isPresent()) {

            return modules.get();
        } else {
            throw new RuntimeException("Modules Not Found.");
        }
    }

    public List<Module> getAllModulesByStudentId() {
        User student = userService.getAuthenticatedUser();
        Optional<List<Module>> moduleList = moduleRepository.findAllBySemesterAndDepartmentId(student.getSemester(), student.getDepartmentId());
        if (moduleList.isEmpty()) throw new RuntimeException("Modules Not Found.");

        return moduleList.get();
    }

    public boolean moduleEnrollment(Integer moduleId, String enrollmentKey) {

        User user = userService.getAuthenticatedUser();
        Module module = getModuleByModuleId(moduleId);

        if (!module.getStudentBatch().equals(user.getBatch())) {
            throw new IllegalArgumentException("This module is not allocate to your batch.");
        }
        if (!module.getSemester().equals(user.getSemester())){
            throw new IllegalArgumentException("This module is not allocate to your current semester.");
        }

        if (Objects.equals(module.getModuleEnrolmentKey(), enrollmentKey)) {
            ModuleEnrollment moduleEnrollment = moduleEnrollmentService.enrollModule(moduleId);
            return moduleEnrollment != null;
        } else {
            throw new IllegalArgumentException("The Enrolment Key Is Not Match.");
        }
    }

    public void moduleUnEnrollment(Integer moduleId){
        User user = userService.getAuthenticatedUser();
        Module module = getModuleByModuleId(moduleId);
        moduleEnrollmentService.unEnrollModule(moduleId);
    }

    public List<Module> getAllEnrolledModulesForStudent() {
        List<ModuleEnrollment> moduleEnrollmentList = moduleEnrollmentService.getAllModuleEnrollmentForStudent();
        List<Module> moduleList = new ArrayList<>();
        for (ModuleEnrollment moduleEnrollment : moduleEnrollmentList) {
            Module module = getModuleByModuleId(moduleEnrollment.getModuleId());
            moduleList.add(module);
        }
        return moduleList;
    }

    public List<Module> getAllUnEnrolledModulesForStudent() {
        List<ModuleEnrollment> moduleEnrollmentList = moduleEnrollmentService.getAllModuleEnrollmentForStudent();
        User student = userService.getAuthenticatedUser();
        List<Module> moduleList = new ArrayList<>();
        if (student.getSemester() == 1 || student.getSemester() == 2) {
            moduleList = moduleRepository.findAllBySemester(student.getSemester()).orElse(new ArrayList<>());
            moduleList.removeIf(module ->
                    moduleEnrollmentList.stream()
                            .anyMatch(enrollment -> enrollment.getModuleId().equals(module.getId()))
            );
        } else {
            moduleList = moduleRepository
                    .findAllBySemesterAndDepartmentIdAndStudentBatch(student.getSemester(), student.getDepartmentId(), student.getBatch())
                    .orElse(new ArrayList<>());
            moduleList.removeIf(module ->
                    moduleEnrollmentList.stream()
                            .anyMatch(enrollment -> enrollment.getModuleId().equals(module.getId()))
            );
        }

        return moduleList;
    }

}
