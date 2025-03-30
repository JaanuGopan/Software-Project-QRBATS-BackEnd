package com.qrbats.qrbats.functionalities.module.controller;

import com.qrbats.qrbats.entity.module.Module;
import com.qrbats.qrbats.functionalities.module.dto.ModuleDto;
import com.qrbats.qrbats.functionalities.module.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/v1/module")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ModuleController {

    private final ModuleService moduleService;


    @GetMapping("get-module-by-module-code")
    public  ResponseEntity<Module> getModuleByModuleCode(@RequestParam String moduleCode){
        try {
            return new  ResponseEntity<>(moduleService.getModuleByModuleCode(moduleCode), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/get-module-by-moduleId-for-student")
    public  ResponseEntity<Module> getModuleByModuleIdForStudent(@RequestParam Integer moduleId){
        try {
            return new  ResponseEntity<>(moduleService.getModuleByModuleIdForStudent(moduleId), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/save-module")
    public ResponseEntity<Module> saveModule(@RequestBody ModuleDto moduleDto){
        try {
            return new ResponseEntity<>(moduleService.saveModule(moduleDto), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("delete-module")
    public ResponseEntity<Void> deleteModule(@RequestParam Integer moduleId){
       try {
           moduleService.deleteModule(moduleId);
           return ResponseEntity.noContent().build();
       } catch (IllegalArgumentException e) {
           throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
       }
    }

    @GetMapping("/get-all-modules-for-user")
    public ResponseEntity<List<Module>> getModuleByLecturerId(){
        try {
            return new ResponseEntity<>(moduleService.getModuleByLecturerId(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    @GetMapping("/get-all-module-by-departmentId/{departmentId}")
    public ResponseEntity<List<Module>> getAllModulesByDepartmentId(@PathVariable Integer departmentId){
        return ResponseEntity.ok(moduleService.getModuleByDepartmentId(departmentId));
    }

    @GetMapping("/get-all-modules-by-studentId")
    public ResponseEntity<?> getAllModulesByStudentId(){
        try {
            return ResponseEntity.ok(moduleService.getAllModulesByStudentId());
        }catch (RuntimeException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/module-enrollment")
    public ResponseEntity<Boolean> moduleEnrollment(
            @RequestParam Integer moduleId,
            @RequestParam String enrollmentKey
    ){
        try {
            return ResponseEntity.ok(moduleService.moduleEnrollment(moduleId,enrollmentKey));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/module-un-enrollment")
    public ResponseEntity<String> moduleUnEnrollment(@RequestParam Integer moduleId){
        try {
            moduleService.moduleUnEnrollment(moduleId);
            return ResponseEntity.ok("Module unenrolled successfully");
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/get-all-enrolled-modules-for-student")
    public ResponseEntity<List<Module>> getAllEnrolledModulesForStudent(){
        try {
            List<Module> modules = moduleService.getAllEnrolledModulesForStudent();
            return new ResponseEntity<>(modules, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/get-all-un-enrolled-module-for-student")
    public ResponseEntity<List<Module>> getAllUnEnrolledModulesForStudent() {
        try {
            List<Module> modules = moduleService.getAllUnEnrolledModulesForStudent();
            return new ResponseEntity<>(modules, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
