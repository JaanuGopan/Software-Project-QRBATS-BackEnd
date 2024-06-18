package com.qrbats.qrbats.functionalities.module_creation.services.impl;

import ch.qos.logback.core.joran.conditional.IfAction;
import com.qrbats.qrbats.authentication.entities.student.Student;
import com.qrbats.qrbats.authentication.entities.student.repository.StudentRepository;
import com.qrbats.qrbats.entity.module.Module;
import com.qrbats.qrbats.entity.module.ModuleRepository;
import com.qrbats.qrbats.entity.moduleenrolment.ModuleEnrolment;
import com.qrbats.qrbats.functionalities.module_creation.dto.ModuleCreationRequest;
import com.qrbats.qrbats.functionalities.module_creation.dto.ModuleDeletionRequest;
import com.qrbats.qrbats.functionalities.module_creation.dto.ModuleUpdateRequest;
import com.qrbats.qrbats.functionalities.module_creation.services.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ModuleServiceImpl implements ModuleService {

    @Autowired
    private final ModuleRepository moduleRepository;
    @Autowired
    private final StudentRepository studentRepository;

    @Autowired
    private final ModuleEnrollmentService moduleEnrollmentService;

    @Override
    public Module getModuleByModuleCode(String moduleCode) {
        Optional<Module> module = moduleRepository.findByModuleCode(moduleCode);
        if (!module.isPresent()) throw new RuntimeException("There Is No Module For This ModuleCode "+ moduleCode);
        return module.get();
    }

    @Override
    public Module createModule(ModuleCreationRequest moduleCreationRequest) {

        Optional<Module> checkModuleCode = moduleRepository.findByModuleCode(moduleCreationRequest.getModuleCode());
        if (checkModuleCode.isPresent())
            throw new RuntimeException("The ModuleCode " + moduleCreationRequest.getModuleCode()
                    + " Already Created.");

        Module module = new Module();
        module.setModuleCode(moduleCreationRequest.getModuleCode());
        module.setModuleName(moduleCreationRequest.getModuleName());
        module.setModuleEnrolmentKey(moduleCreationRequest.getModuleEnrolmentKey());
        module.setSemester(moduleCreationRequest.getSemester());
        module.setLecturerId(moduleCreationRequest.getUserId());
        module.setDepartmentId(moduleCreationRequest.getDepartmentId());


        Module save = moduleRepository.save(module);
        moduleEnrollmentService.createModuleEnrollmentTable(save.getModuleId());
        return save;

    }


    @Override
    public void deleteModule(Integer moduleId) {
        Integer deleteModuleId = moduleId;
        Optional<Module> deleteModule = moduleRepository.findById(deleteModuleId);
        if (deleteModule.isPresent()) {
            moduleRepository.delete(deleteModule.get());
            moduleEnrollmentService.deleteModuleEnrollmentTable(moduleId);
        } else {
            throw new RuntimeException("module not found for delete.");
        }
    }

    @Override
    public Module updateModule(ModuleUpdateRequest moduleUpdateRequest) {
        Optional<Module> oldModule = moduleRepository.findById(moduleUpdateRequest.getModuleId());
        if (!oldModule.isPresent()) throw new RuntimeException("There Is No Module For This Id");

        Optional<Module> checkModuleCode = moduleRepository.findByModuleCode(moduleUpdateRequest.getModuleCode());
        if (checkModuleCode.isPresent()) {
            if (oldModule.get().getModuleCode() != checkModuleCode.get().getModuleCode()) {
                throw new RuntimeException("The Updated ModuleCode " + moduleUpdateRequest.getModuleCode() + " Already Exist.");
            }
        }

        if (oldModule.isPresent()) {
            oldModule.get().setModuleCode(moduleUpdateRequest.getModuleCode());
            oldModule.get().setModuleName(moduleUpdateRequest.getModuleName());
            oldModule.get().setModuleEnrolmentKey(moduleUpdateRequest.getModuleEnrolmentKey());
            oldModule.get().setSemester(moduleUpdateRequest.getSemester());
            oldModule.get().setDepartmentId(moduleUpdateRequest.getDepartmentId());
            oldModule.get().setLecturerId(moduleUpdateRequest.getLectureId());

            return moduleRepository.save(oldModule.get());
        } else {
            throw new RuntimeException(moduleUpdateRequest.getModuleCode() + " Module Not Found.");
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
        Optional<List<Module>> moduleList = moduleRepository.findAllBySemesterAndDepartmentId(student.get().getCurrentSemester(), student.get().getDepartmentId());
        if (!moduleList.isPresent()) throw new RuntimeException("Modules Not Found.");

        return moduleList.get();
    }

    @Override
    public boolean moduleEnrollment(Integer moduleId, Integer studentId, String enrollmentKey) {
        Optional<Student> student = studentRepository.findById(studentId);
        if (!student.isPresent()) throw new RuntimeException("Student Not Found.");

        Optional<Module> module = moduleRepository.findById(moduleId);
        if (!module.isPresent()) throw new RuntimeException("There Is No Module For This Id");

        if (Objects.equals(module.get().getModuleEnrolmentKey(), enrollmentKey)) {
            boolean enrollmentStatus = moduleEnrollmentService.studentModuleEnrollment(moduleId, studentId);
            return enrollmentStatus;
        } else {
            throw new RuntimeException("The Enrolment Key Is Not Match.");
        }
    }

    public boolean moduleUnEnrollment(Integer moduleId, Integer studentId){
        Optional<Student> student = studentRepository.findById(studentId);
        if (!student.isPresent()) throw new RuntimeException("Student Not Found.");

        Optional<Module> module = moduleRepository.findById(moduleId);
        if (!module.isPresent()) throw new RuntimeException("There Is No Module For This Id");

        boolean moduleUnEnrollment = moduleEnrollmentService.moduleUnEnrollment(moduleId,studentId);
        if (moduleUnEnrollment){
            return true;
        }else {
            throw new RuntimeException("Error In Module UnEnrollment.");
        }

    }

    @Override
    public List<Module> getAllEnrolledModules(Integer studentId) {
        Optional<Student> student = studentRepository.findById(studentId);
        if (!student.isPresent()) throw new RuntimeException("Student Not Found For This Id.");

        Integer studentDepartmentId = student.get().getDepartmentId();
        Integer studentSemester = student.get().getCurrentSemester();

        Optional<List<Module>> allModules = moduleRepository.findAllBySemesterAndDepartmentId(studentSemester, studentDepartmentId);
        if (!allModules.isPresent()) throw new RuntimeException("There Are No Any Modules For You.");

        List<Module> enrolledModuleList = new ArrayList<>();
        for (Module module : allModules.get()){
            boolean isEnrolled = moduleEnrollmentService.checkStudentEnrollment(module.getModuleId(),studentId);
            if (isEnrolled){
                enrolledModuleList.add(module);
            }
        }
        return enrolledModuleList;
    }

    @Override
    public List<Student> getAllEnrolledStudentByModuleCode(String moduleCode) {
        Optional<Module> module = moduleRepository.findByModuleCode(moduleCode);
        if (!module.isPresent()) throw new RuntimeException("No Module Found For This Module Code "+ moduleCode);
        List<ModuleEnrolment> moduleEnrolmentList = moduleEnrollmentService.getModuleEnrolmentListByModuleId(
                module.get().getModuleId());
        List<Student> studentList = new ArrayList<>();
        for (ModuleEnrolment moduleEnrolment : moduleEnrolmentList){
            Optional<Student> student = studentRepository.findById(moduleEnrolment.getStudentId());
            if (student.isPresent()){
                studentList.add(student.get());
            }
        }
        return studentList;
    }


}
