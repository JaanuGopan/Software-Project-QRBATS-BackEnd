package com.qrbats.qrbats.functionalities.department.service;

import com.qrbats.qrbats.entity.department.Department;
import com.qrbats.qrbats.entity.department.DepartmentCode;
import com.qrbats.qrbats.entity.department.DepartmentName;
import com.qrbats.qrbats.entity.department.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Transactional
    public void createDefaultDepartment() {
        if (departmentRepository.count() != 0) {
            return;
        }

        List<Department> departmentList = List.of(
                new Department(1, DepartmentName.CIVIL_AND_ENVIRONMENTAL_ENGINEERING, DepartmentCode.DCEE),
                new Department(2, DepartmentName.ELECTRICAL_AND_INFORMATION_ENGINEERING, DepartmentCode.DEIE),
                new Department(3, DepartmentName.MECHANICAL_AND_MANUFACTURING_ENGINEERING, DepartmentCode.DMME),
                new Department(4, DepartmentName.INTERDISCIPLINARY_STUDIES, DepartmentCode.DIS),
                new Department(5, DepartmentName.MARINE_ENGINEERING_AND_NAVAL_ARCHITECTURE, DepartmentCode.DMENA),
                new Department(6, DepartmentName.COMPUTER_ENGINEERING, DepartmentCode.DCOM)
        );

        departmentRepository.saveAll(departmentList);
    }

    public List<Department> getAllDepartment() {
        return departmentRepository.findAll();
    }

}
