package com.qrbats.qrbats.functionalities.module_creation.services;

import com.qrbats.qrbats.authentication.entities.user.User;
import com.qrbats.qrbats.authentication.entities.user.repository.UserRepository;
import com.qrbats.qrbats.entity.module.Module;
import com.qrbats.qrbats.entity.module.ModuleRepository;
import com.qrbats.qrbats.entity.moduleenrolment.ModuleEnrolment;
import com.qrbats.qrbats.functionalities.module_creation.dto.ModuleCreationRequest;
import com.qrbats.qrbats.functionalities.module_creation.dto.ModuleUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final UserRepository userRepository;

    private final ModuleEnrollmentService moduleEnrollmentService;

    public Module getModuleByModuleCode(String moduleCode) {
        Optional<Module> module = moduleRepository.findByModuleCode(moduleCode);
        if (!module.isPresent()) throw new RuntimeException("There Is No Module For This ModuleCode "+ moduleCode);
        return module.get();
    }

    public Module createModule(ModuleCreationRequest moduleCreationRequest) {

        Optional<Module> checkModuleCode = moduleRepository.findByModuleCode(moduleCreationRequest.getModuleCode());
        if (checkModuleCode.isPresent())
            throw new RuntimeException("The ModuleCode " + moduleCreationRequest.getModuleCode()
                    + " Already Created.");
        Optional<Module> checkModuleName = moduleRepository.findByModuleName(moduleCreationRequest.getModuleName());
        if (checkModuleName.isPresent()){
            throw new RuntimeException("The Module Name '"+moduleCreationRequest.getModuleName()+"' Already Exist.");
        }

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

    public List<Module> getModuleByLecturerId(Integer lecturerId) {
        Optional<List<Module>> lecturerModulesList = moduleRepository.findAllByLecturerId(lecturerId);
        if (lecturerModulesList.isPresent()) {
            return lecturerModulesList.get();
        } else {
            throw new RuntimeException("Module not found.");
        }
    }

    public List<Module> getModuleBySemesterDepartment(Integer semesterId, Integer departmentId) {
        Optional<List<Module>> moduleList = moduleRepository.findAllBySemesterAndDepartmentId(semesterId, departmentId);
        if (moduleList.isPresent()) {
            return moduleList.get();
        } else {
            throw new RuntimeException("Module not found");
        }
    }

    public List<Module> getModuleByDepartmentId(Integer departmentId) {
        Optional<List<Module>> modules = moduleRepository.findAllByDepartmentId(departmentId);
        if (modules.isPresent()) {

            return modules.get();
        } else {
            throw new RuntimeException("Modules Not Found.");
        }
    }

    public List<Module> getAllModulesByStudentId(Integer studentId) {
        Optional<User> student = userRepository.findById(studentId);
        if (student.isEmpty()) throw new RuntimeException("Student Not Found.");
        Optional<List<Module>> moduleList = moduleRepository.findAllBySemesterAndDepartmentId(student.get().getSemester(), student.get().getDepartmentId());
        if (moduleList.isEmpty()) throw new RuntimeException("Modules Not Found.");

        return moduleList.get();
    }

    public boolean moduleEnrollment(Integer moduleId, Integer studentId, String enrollmentKey) {
        Optional<User> student = userRepository.findById(studentId);
        if (student.isEmpty()) throw new RuntimeException("Student Not Found.");

        Optional<Module> module = moduleRepository.findById(moduleId);
        if (module.isEmpty()) throw new RuntimeException("There Is No Module For This Id");

        if (Objects.equals(module.get().getModuleEnrolmentKey(), enrollmentKey)) {
            return moduleEnrollmentService.studentModuleEnrollment(moduleId, studentId);
        } else {
            throw new RuntimeException("The Enrolment Key Is Not Match.");
        }
    }

    public boolean moduleUnEnrollment(Integer moduleId, Integer studentId){
        Optional<User> student = userRepository.findById(studentId);
        if (student.isEmpty()) throw new RuntimeException("Student Not Found.");

        Optional<Module> module = moduleRepository.findById(moduleId);
        if (module.isEmpty()) throw new RuntimeException("There Is No Module For This Id");

        boolean moduleUnEnrollment = moduleEnrollmentService.moduleUnEnrollment(moduleId,studentId);
        if (moduleUnEnrollment){
            return true;
        }else {
            throw new RuntimeException("Error In Module UnEnrollment.");
        }

    }

    public List<Module> getAllEnrolledModules(Integer studentId) {
        Optional<User> student = userRepository.findById(studentId);
        if (student.isEmpty()) throw new RuntimeException("Student Not Found For This Id.");

        Integer studentDepartmentId = student.get().getDepartmentId();
        Integer studentSemester = student.get().getSemester();

        Optional<List<Module>> allModules = moduleRepository.findAllBySemesterAndDepartmentId(studentSemester, studentDepartmentId);
        if (allModules.isEmpty()) throw new RuntimeException("There Are No Any Modules For You.");

        List<Module> enrolledModuleList = new ArrayList<>();
        for (Module module : allModules.get()){
            boolean isEnrolled = moduleEnrollmentService.checkStudentEnrollment(module.getModuleId(),studentId);
            if (isEnrolled){
                enrolledModuleList.add(module);
            }
        }
        return enrolledModuleList;
    }

    public List<User> getAllEnrolledStudentByModuleCode(String moduleCode) {
        Optional<Module> module = moduleRepository.findByModuleCode(moduleCode);
        if (module.isEmpty()) throw new RuntimeException("No Module Found For This Module Code "+ moduleCode);
        List<ModuleEnrolment> moduleEnrolmentList = moduleEnrollmentService.getModuleEnrolmentListByModuleId(
                module.get().getModuleId());
        List<User> studentList = new ArrayList<>();
        for (ModuleEnrolment moduleEnrolment : moduleEnrolmentList){
            Optional<User> student = userRepository.findById(moduleEnrolment.getStudentId());
            student.ifPresent(studentList::add);
        }
        return studentList;

    }


}
