package com.qrbats.qrbats.authentication.dto;

import lombok.Data;

@Data
public class SigninRequest {
    private String userName;
    private String password;
}
