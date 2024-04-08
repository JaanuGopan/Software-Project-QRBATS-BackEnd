package com.qrbats.qrbats.authentication.entities.otp.otpverification.dto;

import lombok.Data;

@Data
public class StudentOTPVerificationRequest {
    private String studentEmail;
    private String otp;
}
