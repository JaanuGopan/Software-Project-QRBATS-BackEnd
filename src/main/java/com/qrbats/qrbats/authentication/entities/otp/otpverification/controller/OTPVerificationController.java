package com.qrbats.qrbats.authentication.entities.otp.otpverification.controller;


import com.qrbats.qrbats.authentication.entities.otp.otpverification.dto.OTPRequest;
import com.qrbats.qrbats.authentication.entities.otp.otpverification.dto.StudentOTPVerificationRequest;
import com.qrbats.qrbats.authentication.entities.otp.otpverification.service.OTPVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/otp")
@RequiredArgsConstructor
public class OTPVerificationController {

    private final OTPVerificationService otpVerificationService;

    @PostMapping("/generateotp")
    public ResponseEntity<Boolean> sendOTP(@RequestBody OTPRequest otpRequest){
        return ResponseEntity.ok(otpVerificationService.sendOTP(otpRequest.getOtpSendEmail()));
    }

    @PostMapping("/otpverification")
    public ResponseEntity<Boolean> verifyOTP(@RequestBody StudentOTPVerificationRequest studentOTPVerificationRequest){
        return ResponseEntity.ok(otpVerificationService.otpVerification(
                studentOTPVerificationRequest.getStudentEmail(),
                studentOTPVerificationRequest.getOtp()));
    }

}
