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

    @PostMapping("/save-module")
    public ResponseEntity<Module> saveModule(@RequestBody ModuleDto moduleDto){
        try {
            return new ResponseEntity<>(moduleService.saveModule(moduleDto), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
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

    @GetMapping("get-module-by-createdBy")
    public ResponseEntity<List<Module>> getModuleByLecturerId(){
        try {
            return new ResponseEntity<>(moduleService.getModuleByLecturerId(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /*@PostMapping("get-all-module-by-semester-and-departmentId")
    public ResponseEntity<List<Module>> getModuleBySemesterDepartment(
            @RequestBody GatAllModuleBySemesterAndDepartmentIdRequest request
    ) {
      return ResponseEntity.ok(moduleService.getModuleBySemesterDepartment(
              request.getSemester(), request.getDepartmentId()
      ));
    }*/

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
    public ResponseEntity<?> moduleEnrollment(
            @RequestParam Integer moduleId, @RequestParam String enrollmentKey){

        try {
            return ResponseEntity.ok(moduleService.moduleEnrollment(moduleId,enrollmentKey));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/module-un-enrollment")
    public ResponseEntity<Void> moduleUnEnrollment(@RequestParam Integer moduleId){
        try {
            moduleService.moduleUnEnrollment(moduleId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/get-all-enrolled-modules")
    public ResponseEntity<?> getAllEnrolledModules(){
        try {
            return ResponseEntity.ok(moduleService.getAllModulesByStudentId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
