package com.qrbats.qrbats.entity.module;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Module")
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Module_id")
    private Integer moduleId;

    @Column(name = "Module_code")
    private String moduleCode;

    @Column(name = "Module_name")
    private String moduleName;

    @Column(name = "Module_entrolment_key")
    private String moduleEnrolmentKey;

    @Column(name = "Semester")
    private Integer semester;

    @Column(name = "Department_id")
    private Integer departmentId;

    @Column(name = "Lecturer_id")
    private Integer lecturerId;

}
