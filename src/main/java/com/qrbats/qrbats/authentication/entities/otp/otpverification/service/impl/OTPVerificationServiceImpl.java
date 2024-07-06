package com.qrbats.qrbats.authentication.entities.otp.otpverification.service.impl;


import com.qrbats.qrbats.authentication.entities.otp.OTP;
import com.qrbats.qrbats.authentication.entities.otp.otpverification.service.OTPVerificationService;
import com.qrbats.qrbats.authentication.entities.otp.repository.OTPRepository;
import lombok.AllArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
@Component
public class OTPVerificationServiceImpl implements OTPVerificationService {

    private final OTPRepository otpRepository;
    private final JavaMailSender mailSender;

    @Override
    public boolean sendOTP(String email) {
        Optional<OTP> existEmailOtp = otpRepository.findByEmail(email);
        OTP newOTP;
        if (existEmailOtp.isPresent()) {
            newOTP = existEmailOtp.get();
        } else {
            newOTP = new OTP();
            newOTP.setEmail(email);
        }
        newOTP.setCreationTime(LocalDateTime.now());
        newOTP.setOtp(generateOTP());
        otpRepository.save(newOTP);
        sendEmail(newOTP.getEmail(), "OTP Verification", "The OTP is: " + newOTP.getOtp());
        return true;
    }

    @Override
    public void sendEmail(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("sjanugopanstudy@gmail.com");
            message.setTo(toEmail);
            message.setText(body);
            message.setSubject(subject);

            mailSender.send(message);
        } catch (MailException e) {
            throw new RuntimeException("Failed To Send An Email To "+toEmail+".");
        }
    }

    @Override
    public String generateOTP() {
        Random random = new Random();
        int randomNumber = random.nextInt(9999);
        String otp = Integer.toString(randomNumber);
        while (otp.length() < 4) {
            otp = "0" + otp;
        }
        return otp;
    }

    @Override
    public boolean otpVerification(String studentEmail, String otp) {
        Optional<OTP> existStudentOTP = otpRepository.findByEmail(studentEmail);
        if (!existStudentOTP.isPresent()){
            throw new RuntimeException("The Email "+studentEmail+" Is Not Valid.");
        }
        if (existStudentOTP.get().getOtp() == null){
            throw new RuntimeException("There Is No Any OTP Found For This Email "+studentEmail);
        }
        boolean isOtpValid = existStudentOTP.filter(value -> Objects.equals(otp, value.getOtp())).isPresent();
        if (!isOtpValid){
            throw new RuntimeException("The OTP Is Not Match.");
        }
        if (isOtpValid && !existStudentOTP.get().getCreationTime().isAfter(LocalDateTime.now().minusMinutes(5))){
            throw new RuntimeException("The OTP Is Expired.");
        }
        existStudentOTP.get().setOtp(null);
        otpRepository.save(existStudentOTP.get());
        return isOtpValid;
    }
    @Override
    @Scheduled(fixedRate = 60000) // Runs every minute
    public void deleteExpiredOTPs() {
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        otpRepository.deleteByCreationTimeBefore(oneMinuteAgo);
    }
}
