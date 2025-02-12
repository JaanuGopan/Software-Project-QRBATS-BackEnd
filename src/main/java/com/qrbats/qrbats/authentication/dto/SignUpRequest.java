package com.qrbats.qrbats.authentication.dto;

import com.qrbats.qrbats.authentication.entities.user.Role;
import lombok.Data;

@Data
public class SignUpRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String userName;
    private String password;
    private Role role;
    private Integer departmentId;
    private Integer semester;
    private String indexNumber;
}
