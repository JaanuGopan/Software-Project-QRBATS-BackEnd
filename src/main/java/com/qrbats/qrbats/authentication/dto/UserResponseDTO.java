package com.qrbats.qrbats.authentication.dto;

import com.qrbats.qrbats.authentication.entities.user.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private Integer userId;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private Integer departmentId;
    private Integer semester;
    private String indexNumber;
    private Role role;
}
