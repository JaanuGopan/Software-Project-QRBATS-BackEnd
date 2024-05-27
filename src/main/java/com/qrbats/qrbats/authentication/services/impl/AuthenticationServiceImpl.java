package com.qrbats.qrbats.authentication.services.impl;

import com.qrbats.qrbats.authentication.dto.*;
import com.qrbats.qrbats.authentication.entities.user.Role;
import com.qrbats.qrbats.authentication.entities.user.User;
import com.qrbats.qrbats.authentication.entities.user.repository.UserRepository;
import com.qrbats.qrbats.authentication.services.AuthenticationService;
import com.qrbats.qrbats.authentication.services.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public User signup(SignUpRequest signUpRequest) {
        User user = new User();

        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setEmail(signUpRequest.getEmail());
        user.setUserName(signUpRequest.getUserName());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setRole(Role.LECTURER);
        user.setDepartmentId(signUpRequest.getDepartmentId());

        return userRepository.save(user);
    }

    public JwtAuthenticationResponse signin(String userName, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        userName, password
                )
        );

        var user = userRepository.findByUserName(
                userName).orElseThrow(
                () -> new IllegalArgumentException("Invalid userName or password")
        );

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("firstName", user.getFirstName());
        extraClaims.put("lastName", user.getLastName());
        extraClaims.put("email", user.getEmail());
        extraClaims.put("role", user.getRole());
        extraClaims.put("userId", user.getUserId());
        extraClaims.put("departmentId", user.getDepartmentId());

        var jwt = jwtService.generateToken(user, extraClaims);
        var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

        JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();

        jwtAuthenticationResponse.setToken(jwt);
        jwtAuthenticationResponse.setRefreshToken(refreshToken);
        return jwtAuthenticationResponse;
    }

    public JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String userName = jwtService.extractUserName(refreshTokenRequest.getToken());
        User user = userRepository.findByUserName(userName).orElseThrow();

        if (jwtService.isTokenValid(refreshTokenRequest.getToken(), user)) {
            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("firstName", user.getFirstName());
            extraClaims.put("lastName", user.getLastName());
            extraClaims.put("email", user.getEmail());
            extraClaims.put("role", user.getRole());
            extraClaims.put("userId", user.getUserId());
            extraClaims.put("departmentId", user.getDepartmentId());


            var jwt = jwtService.generateToken(user, extraClaims);

            JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();

            jwtAuthenticationResponse.setToken(jwt);
            jwtAuthenticationResponse.setRefreshToken(refreshTokenRequest.getToken());
            return jwtAuthenticationResponse;

        }
        return null;
    }

    @Override
    public List<User> getAllStaffs() {
        List<User> userList = userRepository.findAll();
        userList.removeIf(user -> user.getRole() == Role.ADMIN);
        return userList;
    }

    @Override
    public void deleteByUserId(Integer userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public void updateUser(UpdateUserRequest request) {
        Optional<User> user = userRepository.findById(request.getUserId());
        if (user.isPresent()) {
            user.get().setUserName(request.getUserName());
            user.get().setEmail(request.getEmail());
            user.get().setFirstName(request.getFirstName());
            user.get().setLastName(request.getLastName());
            user.get().setDepartmentId(request.getDepartmentId());
            if (!request.getPassword().isEmpty()) user.get().setPassword(passwordEncoder.encode(request.getPassword()));
            userRepository.save(user.get());
        } else {
            throw new RuntimeException("User not found.");
        }
    }

    @Override
    public Boolean passwordVerification(String userName, String password) {
        Optional<User> user = userRepository.findByUserName(userName);
        if (user.isPresent()) {
            return passwordEncoder.matches(password, user.get().getPassword());
        } else {
            throw new RuntimeException("Requested UserName has not exist.");
        }
    }
}
