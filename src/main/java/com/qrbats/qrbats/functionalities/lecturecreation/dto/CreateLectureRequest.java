package com.qrbats.qrbats.functionalities.lecturecreation.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CreateLectureRequest {
    private Integer lectureAssignedUserId;
    private String moduleCode;
    private Map<String, List<TimeSlot>> times;
}
