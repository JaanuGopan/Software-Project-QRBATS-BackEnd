package com.qrbats.qrbats.entity.department;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "department")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id")
    private Integer departmentId;

    @Column(name = "department_name")
    @Enumerated(EnumType.ORDINAL)
    private DepartmentName departmentName;

    @Column(name = "department_code")
    @Enumerated(EnumType.STRING)
    private DepartmentCode departmentCode;
}
