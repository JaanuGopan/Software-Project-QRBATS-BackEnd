package com.qrbats.qrbats.authentication.dto.mobile;

import com.qrbats.qrbats.authentication.entities.student.StudentRole;
import lombok.Data;

@Data
public class StudentUpdateRequest {
    private Integer id;
    private String studentName;
    private String indexNumber;
    private String studentEmail;
    private String userName;
    private String password;
    private StudentRole studentRole;
    private Integer departmentId;
    private Integer currentSemester;
}
