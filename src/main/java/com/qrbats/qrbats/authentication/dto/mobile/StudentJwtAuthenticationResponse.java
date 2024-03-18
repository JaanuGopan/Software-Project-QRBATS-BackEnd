package com.qrbats.qrbats.authentication.dto.mobile;

import lombok.Data;

@Data
public class StudentJwtAuthenticationResponse {
    private String token;
    private String refreshToken;
}
