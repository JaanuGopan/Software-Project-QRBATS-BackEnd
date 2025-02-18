package com.qrbats.qrbats.functionalities.module.dto;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;
@Data
@AllArgsConstructor
public class ModuleDto {
    @Nullable
    private Integer id;
    private String moduleCode;
    private String moduleName;
    private String moduleEnrolmentKey;
    private Integer semester;
    private Integer studentBatch;
    private Integer departmentId;
    @Nullable
    private Integer createdBy;
    private Date statingDate;
    private Date endingDate;
}
