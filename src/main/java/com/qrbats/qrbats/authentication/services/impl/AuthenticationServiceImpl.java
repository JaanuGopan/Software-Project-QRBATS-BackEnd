package com.qrbats.qrbats.authentication.services.impl;

import com.qrbats.qrbats.authentication.dto.*;
import com.qrbats.qrbats.authentication.entities.otp.otpverification.service.OTPVerificationService;
import com.qrbats.qrbats.authentication.entities.user.Role;
import com.qrbats.qrbats.authentication.entities.user.User;
import com.qrbats.qrbats.authentication.entities.user.repository.UserRepository;
import com.qrbats.qrbats.authentication.services.AuthenticationService;
import com.qrbats.qrbats.authentication.services.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final AuthenticationManager authenticationManager;
    @Autowired
    private final JWTService jwtService;

    @Autowired
    private final OTPVerificationService otpVerificationService;

    public User signup(SignUpRequest signUpRequest) {

        Optional<User> existUserByUserName = userRepository.findByUserName(signUpRequest.getUserName());
        if (existUserByUserName.isPresent()) throw new RuntimeException("The UserName Already Exist.");
        Optional<User> existUserByEmail = userRepository.findByEmail(signUpRequest.getEmail());
        if (existUserByEmail.isPresent()) throw new RuntimeException("The Email Already Exist.");


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
    public boolean updateUser(UpdateUserRequest request) {
        Optional<User> user = userRepository.findById(request.getUserId());
        if (!user.isPresent()) {
            throw new RuntimeException("User not found for this id.");
        }

        if (!request.getEmail().equals(user.get().getEmail())) {
            Optional<User> existUserByEmail = userRepository.findByEmail(request.getEmail());
            if (existUserByEmail.isPresent()) throw new RuntimeException("The Email Address Already Exist.");
        }

        if (!request.getUserName().equals(user.get().getUsername())) {
            Optional<User> existUserByUserName = userRepository.findByUserName(request.getUserName());
            if (existUserByUserName.isPresent()) throw new RuntimeException("User Name Already Exist.");
        }

        if (!request.getUserName().isEmpty()) user.get().setUserName(request.getUserName());
        if (!request.getEmail().isEmpty()) user.get().setEmail(request.getEmail());
        if (!request.getFirstName().isEmpty()) user.get().setFirstName(request.getFirstName());
        if (!request.getLastName().isEmpty()) user.get().setLastName(request.getLastName());
        if (request.getDepartmentId() != null) user.get().setDepartmentId(request.getDepartmentId());
        if (!request.getPassword().isEmpty()) user.get().setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user.get());
        return true;
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

    @Override
    public Boolean forgotPasswordSendEmail(String email) {
        Optional<User> existUser = userRepository.findByEmail(email);
        if (!existUser.isPresent()) throw new RuntimeException("No User Found For This Email " + email);
        Boolean isSendOtp = otpVerificationService.sendOTP(email);

        return isSendOtp;

    }

    @Override
    public Boolean forgotPasswordOtpVerification(String email, String otp) {
        Optional<User> existUser = userRepository.findByEmail(email);
        if (!existUser.isPresent()) throw new RuntimeException("No User Found For This Email " + email);

        Boolean isVerified = otpVerificationService.otpVerification(email,otp);

        return isVerified;
    }

    @Override
    public Boolean forgotPasswordResetPassword(String email, String password, String userName) {
        Optional<User> existUser = userRepository.findByEmail(email);
        if (!existUser.isPresent()) throw new RuntimeException("No User Found For This Email " + email);

        existUser.get().setUserName(userName);
        existUser.get().setPassword(passwordEncoder.encode(password));
        userRepository.save(existUser.get());
        return true;
    }


}
