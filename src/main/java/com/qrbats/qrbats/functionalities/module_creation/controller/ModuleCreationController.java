package com.qrbats.qrbats.functionalities.module_creation.controller;

import com.qrbats.qrbats.entity.module.Module;
import com.qrbats.qrbats.functionalities.module_creation.dto.*;
import com.qrbats.qrbats.functionalities.module_creation.services.impl.ModuleServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/module")
@CrossOrigin("*")
@RequiredArgsConstructor
public class ModuleCreationController {

    private final ModuleServiceImpl moduleService;

    @PostMapping("createmodule")
    public Module createModule(@RequestBody ModuleCreationRequest request){
        return moduleService.createModule(request);
    }

    @DeleteMapping("deletemodule/{moduleId}")
    public void deleteModule(@PathVariable Integer  moduleId){
        moduleService.deleteModule(moduleId);
    }

    @PutMapping("updatemodule")
    public void updateModule(@RequestBody ModuleUpdateRequest moduleUpdateRequest){
        moduleService.updateModule(moduleUpdateRequest);
    }

    @GetMapping("getmodulebylecturerid/{userId}")
    public ResponseEntity<List<Module>> getModuleByLecturerId(
            @PathVariable String userId
    ){
        return ResponseEntity.ok(
                moduleService.getModuleByLecturerId(Integer.parseInt(userId))
        );
    }

    @PostMapping("getallmodulebysemesteranddepartmentid")
    public ResponseEntity<List<Module>> getModuleBySemesterDepartment(
            @RequestBody GatAllModuleBySemesterAndDepartmentIdRequest request
    ) {
      return ResponseEntity.ok(moduleService.getModuleBySemesterDepartment(
              request.getSemester(), request.getDepartmentId()
      ));
    }

    @GetMapping("/getallmodulebydepartmentid/{departmentId}")
    public ResponseEntity<List<Module>> getAllModulesByDepartmentId(@PathVariable Integer departmentId){
        return ResponseEntity.ok(moduleService.getModuleByDepartmentId(departmentId));
    }






}
