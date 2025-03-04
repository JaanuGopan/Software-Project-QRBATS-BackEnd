package com.qrbats.qrbats.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException e) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", e.getStatusCode().value());
        errorDetails.put("error", e.getStatusCode().toString());
        errorDetails.put("message", e.getReason());

        return ResponseEntity.status(e.getStatusCode()).body(errorDetails);
    }
}
