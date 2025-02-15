package com.qrbats.qrbats.functionalities.otp.repository;

import com.qrbats.qrbats.functionalities.otp.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
@Repository
public interface OTPRepository extends JpaRepository<OTP,Integer> {
    Optional<OTP> findByEmail(String email);
    Optional<OTP> findByOtp(String otp);
    void deleteByCreationTimeBefore(LocalDateTime time);

}
