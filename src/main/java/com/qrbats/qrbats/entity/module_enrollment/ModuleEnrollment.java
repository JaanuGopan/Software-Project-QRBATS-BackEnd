package com.qrbats.qrbats.entity.module_enrollment;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModuleEnrollment {
    @Id
    private Long id;
    private Integer moduleId;
    private Integer studentId;
}
