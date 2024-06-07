package com.qrbats.qrbats.authentication.dto.mobile;

import com.qrbats.qrbats.authentication.entities.student.StudentRole;
import jakarta.persistence.Column;
import lombok.Data;

@Data
public class AdminCreateStudentRequest {
    private Integer studentId;
    private String studentName;
    private String indexNumber;
    private  String studentEmail;
    private String userName;
    private String password;
    private Integer currentSemester;
    private Integer departmentId;
    private StudentRole studentRole;
}
