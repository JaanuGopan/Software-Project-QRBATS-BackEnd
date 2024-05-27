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
    void updateUser(UpdateUserRequest request);

    Boolean passwordVerification(String userName,String password);
}
