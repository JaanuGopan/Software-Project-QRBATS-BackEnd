package com.qrbats.qrbats.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.qrbats.qrbats.entity.attendance.AttendanceData;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class AttendanceDataJsonConverter implements AttributeConverter<AttendanceData, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public String convertToDatabaseColumn(AttendanceData attendanceData) {
        try {
            return objectMapper.writeValueAsString(attendanceData);
        } catch (JsonProcessingException jpe) {
            return null;
        }
    }

    @Override
    public AttendanceData convertToEntityAttribute(String value) {
        try {
            return objectMapper.readValue(value, AttendanceData.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
