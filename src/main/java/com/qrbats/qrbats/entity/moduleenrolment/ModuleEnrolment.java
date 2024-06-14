package com.qrbats.qrbats.entity.moduleenrolment;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleEnrolment {
    @Id
    private Integer moduleEnrollmentId;
    private Integer moduleId;
    private Integer StudentId;
}
