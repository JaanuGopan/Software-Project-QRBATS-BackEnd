package com.qrbats.qrbats.functionalities.module.service;

import com.qrbats.qrbats.config.AuditInfoProvider;
import com.qrbats.qrbats.entity.module_enrollment.ModuleEnrollment;
import com.qrbats.qrbats.entity.module_enrollment.ModuleEnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ModuleEnrollmentService {
    private final ModuleEnrollmentRepository moduleEnrollmentRepository;
    private final AuditInfoProvider auditInfoProvider;


    public ModuleEnrollment getModuleEnrollmentById(Long id) {
        return moduleEnrollmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ModuleEnrollment not found for this id - " + id));
    }

    public List<ModuleEnrollment> getModuleEnrollmentByModuleId(Integer moduleId) {
        return moduleEnrollmentRepository.findAllByModuleId(moduleId)
                .orElseThrow(() -> new IllegalArgumentException("ModuleEnrollment not found for this module."));
    }

    public ModuleEnrollment getModuleEnrollmentByStudentIdAndModuleId(Integer studentId, Integer moduleId) {
        return moduleEnrollmentRepository.findByStudentIdAndModuleId(studentId, moduleId)
                .orElseThrow(() -> new IllegalArgumentException("Student not enrolled to this module."));
    }

    public void deleteAllEnrollmentByModuleId(Integer moduleId) {
        moduleEnrollmentRepository.deleteAllByModuleId(moduleId);
    }

    public ModuleEnrollment enrollModule(Integer moduleId) {
        ModuleEnrollment moduleEnrollment = new ModuleEnrollment();
        moduleEnrollment.setStudentId(auditInfoProvider.getCurrentUserIdentifier());
        moduleEnrollment.setModuleId(moduleId);
        return moduleEnrollmentRepository.save(moduleEnrollment);
    }

    public void unEnrollModule(Integer moduleId) {
        ModuleEnrollment moduleEnrollment = getModuleEnrollmentByStudentIdAndModuleId(
                auditInfoProvider.getCurrentUserIdentifier(),
                moduleId
        );
        moduleEnrollmentRepository.delete(moduleEnrollment);
    }

    public List<ModuleEnrollment> getAllModuleEnrollmentForStudent() {
        return moduleEnrollmentRepository
                .findAllByStudentId(auditInfoProvider.getCurrentUserIdentifier()).orElse(new ArrayList<>());
    }
}
