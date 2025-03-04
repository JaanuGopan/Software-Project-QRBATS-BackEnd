package com.qrbats.qrbats.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qrbats.qrbats.entity.attendance.LectureAttendance;
import jakarta.annotation.Resource;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.HashMap;
import java.util.Map;

@Converter(autoApply = true)
public class AttendanceDataJsonConverter implements AttributeConverter<Map<Integer, Map<Long, LectureAttendance>>, String> {

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(Map<Integer, Map<Long, LectureAttendance>> attendanceData) {
        try {
            return objectMapper.writeValueAsString(attendanceData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting attendanceData to JSON", e);
        }
    }

    @Override
    public Map<Integer, Map<Long, LectureAttendance>> convertToEntityAttribute(String json) {
        if (json == null || json.isEmpty()) {
            return new HashMap<>(); // Return empty map instead of null
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<Integer, Map<Long, LectureAttendance>>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON to attendanceData", e);
        }
    }
}

