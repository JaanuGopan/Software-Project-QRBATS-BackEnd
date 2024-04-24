package com.qrbats.qrbats.authentication.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private Integer userId;
    private String firstName;
    private String lastName;
    private String email;
    private String userName;
    private Integer departmentId;
}
