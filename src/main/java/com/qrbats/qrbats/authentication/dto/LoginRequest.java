package com.qrbats.qrbats.authentication.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String userName;
    private String password;
}
