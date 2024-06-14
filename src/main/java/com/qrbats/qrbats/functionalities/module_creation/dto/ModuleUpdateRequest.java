package com.qrbats.qrbats.functionalities.module_creation.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class ModuleUpdateRequest {
    private Integer moduleId;
    private String moduleCode;
    private String moduleName;
    private String moduleEnrolmentKey;
    private Integer semester;
    private Integer departmentId;
    private Integer lectureId;
}
