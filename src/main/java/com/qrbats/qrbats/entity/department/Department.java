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
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer departmentId;

    @Enumerated(EnumType.ORDINAL)
    private DepartmentName departmentName;

    @Enumerated(EnumType.STRING)
    private DepartmentCode departmentCode;
}
