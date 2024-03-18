package com.qrbats.qrbats.functionalities.module_creation;

import com.qrbats.qrbats.entity.module.Module;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/module")
@CrossOrigin("http://localhost:3000")
@RequiredArgsConstructor
public class ModuleCreationController {

    private final ModuleCreationService moduleCreationService;

    @PostMapping("createmodule")
    public Module createModule(@RequestBody ModuleCreationRequest request){
        return moduleCreationService.createModule(request);
    }

    @DeleteMapping("deletemodule")
    public String deleteModule(@RequestBody ModuleDeletionRequest moduleDeletionRequest){
        moduleCreationService.deleteModule(moduleDeletionRequest);
        return "module deleted.";
    }



}
