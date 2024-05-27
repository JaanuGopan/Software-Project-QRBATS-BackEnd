package com.qrbats.qrbats.functionalities.module_creation.dto;

import lombok.Data;

@Data
public class GatAllModuleBySemesterAndDepartmentIdRequest {
    private Integer semester;
    private Integer departmentId;
}
