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
@Table(name = "Department")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Department_id")
    private Integer departmentId;

    @Column(name = "Department_name")
    @Enumerated(EnumType.STRING)
    private DepartmentName departmentName;
}
