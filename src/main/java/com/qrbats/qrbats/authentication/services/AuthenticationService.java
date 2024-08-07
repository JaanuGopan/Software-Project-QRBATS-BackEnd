package com.qrbats.qrbats.authentication.services;

import com.qrbats.qrbats.authentication.dto.*;
import com.qrbats.qrbats.authentication.entities.user.User;

import java.util.List;

public interface AuthenticationService {
    User signup(SignUpRequest signUpRequest);

    JwtAuthenticationResponse signin(String userName,String password);

    JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

    List<User> getAllStaffs();

    void deleteByUserId(Integer userId);
    boolean updateUser(UpdateUserRequest request);

    Boolean passwordVerification(String userName,String password);

    Boolean forgotPasswordSendEmail(String email);
    Boolean forgotPasswordOtpVerification(String email , String otp);
    Boolean forgotPasswordResetPassword(String email, String password , String userName);
}
