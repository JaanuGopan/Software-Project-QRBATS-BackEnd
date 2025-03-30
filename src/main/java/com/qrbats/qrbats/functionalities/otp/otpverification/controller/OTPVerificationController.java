package com.qrbats.qrbats.functionalities.otp.otpverification.controller;


import com.qrbats.qrbats.functionalities.lecture.dto.LectureViewDto;
import com.qrbats.qrbats.functionalities.otp.otpverification.dto.OTPRequest;
import com.qrbats.qrbats.functionalities.otp.otpverification.dto.StudentOTPVerificationRequest;
import com.qrbats.qrbats.functionalities.otp.otpverification.service.OTPVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/otp")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OTPVerificationController {

    private final OTPVerificationService otpVerificationService;

    @PostMapping("/generate-otp")
    public ResponseEntity<Boolean> sendOTP(@RequestBody OTPRequest otpRequest){
        try {
            return ResponseEntity.ok(otpVerificationService.sendOTP(otpRequest.getOtpSendEmail()));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/otp-verification")
    public ResponseEntity<Boolean> verifyOTP(@RequestBody StudentOTPVerificationRequest studentOTPVerificationRequest){
        try {
            return new ResponseEntity<>(otpVerificationService.otpVerification(
                    studentOTPVerificationRequest.getStudentEmail(),
                    studentOTPVerificationRequest.getOtp()), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
