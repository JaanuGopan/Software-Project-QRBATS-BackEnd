package com.qrbats.qrbats.functionalities.otp.otpverification.controller;


import com.qrbats.qrbats.functionalities.otp.otpverification.dto.OTPRequest;
import com.qrbats.qrbats.functionalities.otp.otpverification.dto.StudentOTPVerificationRequest;
import com.qrbats.qrbats.functionalities.otp.otpverification.service.OTPVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/otp")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OTPVerificationController {

    private final OTPVerificationService otpVerificationService;

    @PostMapping("/generate-otp")
    public ResponseEntity<Boolean> sendOTP(@RequestBody OTPRequest otpRequest){
        return ResponseEntity.ok(otpVerificationService.sendOTP(otpRequest.getOtpSendEmail()));
    }

    @PostMapping("/otp-verification")
    public ResponseEntity<Boolean> verifyOTP(@RequestBody StudentOTPVerificationRequest studentOTPVerificationRequest){
        return ResponseEntity.ok(otpVerificationService.otpVerification(
                studentOTPVerificationRequest.getStudentEmail(),
                studentOTPVerificationRequest.getOtp()));
    }

}
