package com.qrbats.qrbats.authentication.entities.otp.otpverification.service;

public interface OTPVerificationService {

     boolean sendOTP(String email);
     void sendEmail(String toEmail,String subject, String boady);
     String generateOTP();
     void deleteExpiredOTPs();

     boolean otpVerification(String studentEmail,String otp);

}
