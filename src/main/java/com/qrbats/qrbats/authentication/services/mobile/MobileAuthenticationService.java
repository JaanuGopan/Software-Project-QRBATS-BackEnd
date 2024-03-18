package com.qrbats.qrbats.authentication.services.mobile;

import com.qrbats.qrbats.authentication.dto.mobile.StudentSigninRequest;
import com.qrbats.qrbats.authentication.dto.JwtAuthenticationResponse;
import com.qrbats.qrbats.authentication.dto.RefreshTokenRequest;
import com.qrbats.qrbats.authentication.dto.mobile.StudentSignUpRequest;
import com.qrbats.qrbats.authentication.entities.student.Student;

public interface MobileAuthenticationService {
    Student signup(StudentSignUpRequest studentSignUpRequest);

    JwtAuthenticationResponse signin(StudentSigninRequest studentSigninRequest);

    JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}
