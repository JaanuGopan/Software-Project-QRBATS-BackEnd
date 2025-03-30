package com.qrbats.qrbats.authentication.dto;

import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private String firstName;
    @Nullable
    private String lastName;
    private String email;
    @Nullable
    private Integer departmentId;
    @Nullable
    private String userName;
    @Nullable
    private String password;
    @Nullable
    private Integer semester;
    @Nullable
    private String indexNumber;
    @Nullable
    private Integer studentBatch;
}
