package com.qrbats.qrbats.functionalities.attendance.dto;

import lombok.Data;

@Data
public class GetAllStudentsByDeptIdAndSemRequest {
    private Integer departmentID;
    private Integer semester;
}
