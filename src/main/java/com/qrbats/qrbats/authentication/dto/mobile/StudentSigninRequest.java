package com.qrbats.qrbats.authentication.dto.mobile;

import lombok.Data;

@Data
public class StudentSigninRequest {

    private String studentUserName;
    private String password;
}
