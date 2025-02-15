package com.qrbats.qrbats.functionalities.module_creation.controller;

import com.qrbats.qrbats.entity.module.Module;
import com.qrbats.qrbats.functionalities.module_creation.dto.*;
import com.qrbats.qrbats.functionalities.module_creation.services.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/module")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ModuleCreationController {

    private final ModuleService moduleService;


    @GetMapping("get-module-by-module-code")
    public  ResponseEntity<?> getModuleByModuleCode(@RequestParam String moduleCode){
        try {
            return ResponseEntity.ok(moduleService.getModuleByModuleCode(moduleCode));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/create-module")
    public ResponseEntity<?> createModule(@RequestBody ModuleCreationRequest request){
        try {
            return ResponseEntity.ok(moduleService.createModule(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("delete-module/{moduleId}")
    public void deleteModule(@PathVariable Integer  moduleId){
        moduleService.deleteModule(moduleId);
    }

    @PutMapping("/update-module")
    public ResponseEntity<?> updateModule(@RequestBody ModuleUpdateRequest moduleUpdateRequest){
        try {
            return ResponseEntity.ok(moduleService.updateModule(moduleUpdateRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("get-module-by-lecturerId/{userId}")
    public ResponseEntity<List<Module>> getModuleByLecturerId(
            @PathVariable String userId
    ){
        return ResponseEntity.ok(
                moduleService.getModuleByLecturerId(Integer.parseInt(userId))
        );
    }

    @PostMapping("get-all-module-by-semester-and-departmentId")
    public ResponseEntity<List<Module>> getModuleBySemesterDepartment(
            @RequestBody GatAllModuleBySemesterAndDepartmentIdRequest request
    ) {
      return ResponseEntity.ok(moduleService.getModuleBySemesterDepartment(
              request.getSemester(), request.getDepartmentId()
      ));
    }

    @GetMapping("/get-all-module-by-departmentId/{departmentId}")
    public ResponseEntity<List<Module>> getAllModulesByDepartmentId(@PathVariable Integer departmentId){
        return ResponseEntity.ok(moduleService.getModuleByDepartmentId(departmentId));
    }

    @GetMapping("/get-all-modules-by-studentId/{studentId}")
    public ResponseEntity<?> getAllModulesByStudentId(@PathVariable Integer studentId){
        try {
            return ResponseEntity.ok(moduleService.getAllModulesByStudentId(studentId));
        }catch (RuntimeException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/module-enrollment")
    public ResponseEntity<?> moduleEnrollment(
            @RequestParam Integer moduleId,@RequestParam Integer studentId,@RequestParam String enrollmentKey){

        try {
            return ResponseEntity.ok(moduleService.moduleEnrollment(moduleId,studentId,enrollmentKey));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/module-un-enrollment")
    public ResponseEntity<?> moduleUnEnrollment(@RequestParam Integer moduleId,@RequestParam Integer studentId){
        try {
            return ResponseEntity.ok(moduleService.moduleUnEnrollment(moduleId,studentId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get-all-enrolled-modules/{studentId}")
    public ResponseEntity<?> getAllEnrolledModules(@PathVariable Integer studentId){
        try {
            return ResponseEntity.ok(moduleService.getAllEnrolledModules(studentId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
