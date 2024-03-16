package com.qrbats.qrbats.services;

import com.qrbats.qrbats.dto.JwtAuthenticationResponse;
import com.qrbats.qrbats.dto.RefreshTokenRequest;
import com.qrbats.qrbats.dto.SignUpRequest;
import com.qrbats.qrbats.dto.SigninRequest;
import com.qrbats.qrbats.entities.user.User;

public interface AuthenticationService {
    User signup(SignUpRequest signUpRequest);

    JwtAuthenticationResponse signin(SigninRequest signinRequest);

    JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}
