package com.qrbats.qrbats.functionalities.attendance.dto;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Data;

@Data
public class AttendanceStudentHistoryRequest {
    private Integer studentId;
}
