package com.qrbats.qrbats.entity.module;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Entity
@Data
@AllArgsConstructor
@Table
@NoArgsConstructor
public class Module {
    @Id
    private Integer id;
    private String moduleCode;
    private String moduleName;
    private String moduleEnrolmentKey;
    private Integer semester;
    private Integer studentBatch;
    private Integer departmentId;
    private Integer createdBy;
    private Date statingDate;
    private Date endingDate;
}
