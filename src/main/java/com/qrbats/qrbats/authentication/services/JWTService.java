package com.qrbats.qrbats.authentication.services;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface JWTService {

    String extractUserName(String token);
    String generateToken(UserDetails userDetails, Map<String, Object> extraClaims);

    boolean isTokenValid(String token, UserDetails userDetails);
    boolean isTokenExpired(String token);

    String generateRefreshToken(Map<String, Object> extraClaims , UserDetails userDetails);

}
