package com.qrbats.qrbats.entity.module;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String moduleCode;
    private String moduleName;
    private String moduleEnrolmentKey;
    private Integer semester;
    private Integer studentBatch;
    private Integer departmentId;
    private Integer createdBy;
    private LocalDate statingDate;
    private LocalDate endingDate;
}
