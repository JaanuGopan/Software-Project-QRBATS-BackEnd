package com.qrbats.qrbats.authentication.services;

import com.qrbats.qrbats.authentication.dto.*;
import com.qrbats.qrbats.config.AuditInfoProvider;
import com.qrbats.qrbats.functionalities.otp.otpverification.service.OTPVerificationService;
import com.qrbats.qrbats.authentication.entities.user.Role;
import com.qrbats.qrbats.authentication.entities.user.User;
import com.qrbats.qrbats.authentication.entities.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final AuditInfoProvider auditInfoProvider;

    private final OTPVerificationService otpVerificationService;


    public Boolean checkStudentSignupDetails(String email, String indexNumber, String deviceId) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("For this email address there is already have an account.");
        }
        if (userRepository.findByIndexNumber(indexNumber).isPresent()) {
            throw new IllegalArgumentException("The index number already taken.");
        }
        if (userRepository.findByDeviceId(deviceId).isPresent()) {
            throw new IllegalArgumentException("This device is already linked to a different account. Please use a different device.");
        }

        return true;
    }

    public User signup(SignUpRequest signUpRequest) {

        Optional<User> existUserByUserName = userRepository.findByUserName(signUpRequest.getUserName());
        if (existUserByUserName.isPresent()) throw new IllegalArgumentException("The username already exist.");
        Optional<User> existUserByEmail = userRepository.findByEmail(signUpRequest.getEmail());
        if (existUserByEmail.isPresent()) throw new IllegalArgumentException("The email already exist.");
        if (signUpRequest.getIndexNumber() != null && userRepository
                .findByIndexNumber(signUpRequest.getIndexNumber()).isPresent()) {
            throw new IllegalArgumentException("The Student index number is already exist.");
        }

        User user = new User();
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setEmail(signUpRequest.getEmail());
        user.setUserName(signUpRequest.getUserName());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setRole(signUpRequest.getRole());
        user.setDepartmentId(signUpRequest.getDepartmentId() == -1 ? null : signUpRequest.getDepartmentId());
        user.setSemester(signUpRequest.getSemester());
        user.setIndexNumber(signUpRequest.getIndexNumber());
        user.setRole(signUpRequest.getRole());
        user.setBatch(signUpRequest.getBatch());
        user.setDeviceId(signUpRequest.getDeviceId());

        return userRepository.save(user);
    }

    public JwtAuthenticationResponse staffLogin(String userName, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, password));
        } catch (AuthenticationException e) {
            throw new IllegalArgumentException("Invalid userName or password.");
        }

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid userName or password."));

        if (user.getRole().equals(Role.STUDENT)) {
            throw new IllegalArgumentException("You are not access to this site.");
        }

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", user.getUserId());

        var jwt = jwtService.generateToken(user, extraClaims);
        var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

        JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();

        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setUserId(user.getUserId());
        userResponseDTO.setEmail(user.getEmail());
        userResponseDTO.setRole(user.getRole());
        userResponseDTO.setSemester(user.getSemester());
        userResponseDTO.setFirstName(user.getFirstName());
        userResponseDTO.setDepartmentId(user.getDepartmentId());
        userResponseDTO.setIndexNumber(user.getIndexNumber());
        userResponseDTO.setLastName(user.getLastName());
        userResponseDTO.setUserName(user.getUsername());

        jwtAuthenticationResponse.setToken(jwt);
        jwtAuthenticationResponse.setRefreshToken(refreshToken);
        jwtAuthenticationResponse.setUser(userResponseDTO);
        return jwtAuthenticationResponse;
    }

    public boolean verifyDeviceForStudent(String userName, String password, String deviceId) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, password));
        } catch (AuthenticationException e) {
            throw new IllegalArgumentException("Invalid username or password.");
        }

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));

        if (!user.getRole().equals(Role.STUDENT)) {
            throw new IllegalArgumentException("You are not access to this site.");
        }

        return user.getDeviceId().equals(deviceId);
    }

    public Boolean sendOtpForDeviceVerification(String email, String username, String password, String deviceId) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (AuthenticationException e) {
            throw new IllegalArgumentException("Invalid username or password.");
        }

        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));

        if (!user.getEmail().equals(email)) {
           throw new IllegalArgumentException("Invalid email address for your account.");
        }

        if (!user.getRole().equals(Role.STUDENT)) {
            throw new IllegalArgumentException("You are not access to this app.");
        }

        List<User> studentList = userRepository.findALLByRole(Role.STUDENT);
        for (User student : studentList) {
            if (student.getDeviceId() != null && student.getDeviceId().equals(deviceId)) {
                throw new IllegalArgumentException("This device is already linked to a different account. Please use a different device.");
            }
        }

        return otpVerificationService.sendOTP(email);
    }

    public Boolean otpVerificationForDeviceVerification(String email, String otp, String deviceId) {
        if (otpVerificationService.otpVerification(email, otp)) {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Invalid email address."));
            if (userRepository.findByDeviceId(deviceId).isEmpty()) {
                user.setDeviceId(deviceId);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    public JwtAuthenticationResponse studentLogin(String userName, String password, String deviceId) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, password));
        } catch (AuthenticationException e) {
            throw new IllegalArgumentException("Invalid userName or password.");
        }

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid userName or password"));

        if (!user.getRole().equals(Role.STUDENT)) {
            throw new IllegalArgumentException("You are not access to this site.");
        }

        if (user.getDeviceId() != null && !user.getDeviceId().equals(deviceId)) {
            throw new IllegalArgumentException("Login failed: This account is registered on a different device. Please use the original device or verify your new device.");
        }


        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("firstName", user.getFirstName());
        extraClaims.put("email", user.getEmail());
        extraClaims.put("userId", user.getUserId());
        if (user.getDepartmentId() != null) extraClaims.put("departmentId", user.getDepartmentId());
        extraClaims.put("semester", user.getSemester());
        extraClaims.put("indexNumber", user.getIndexNumber());
        extraClaims.put("batch", user.getBatch());

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

            var jwt = jwtService.generateToken(user, extraClaims);

            JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();

            jwtAuthenticationResponse.setToken(jwt);
            jwtAuthenticationResponse.setRefreshToken(refreshTokenRequest.getToken());
            return jwtAuthenticationResponse;

        }
        return null;
    }

    public boolean updateUser(UpdateUserRequest request) {
        Optional<User> user = userRepository.findById(auditInfoProvider.getCurrentUserIdentifier());
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found for this id.");
        }

        if (request.getEmail() != null) {
            if (!request.getEmail().equals(user.get().getEmail())) {
                Optional<User> existUserByEmail = userRepository.findByEmail(request.getEmail());
                if (existUserByEmail.isPresent())
                    throw new IllegalArgumentException("The email address already exist.");
            }
        }

        if (request.getUserName() != null) {
            if (!request.getUserName().equals(user.get().getUsername())) {
                Optional<User> existUserByUserName = userRepository.findByUserName(request.getUserName());
                if (existUserByUserName.isPresent()) throw new IllegalArgumentException("Username already exist.");
            }
        }

        if (user.get().getIndexNumber() != null) {
            assert request.getIndexNumber() != null;
            if (!request.getIndexNumber().equals(user.get().getIndexNumber())) {
                Optional<User> existUserByUserName = userRepository.findByIndexNumber(request.getIndexNumber());
                if (existUserByUserName.isPresent())
                    throw new IllegalArgumentException("This student index number is already exist.");
            }
        }

        if (request.getUserName() != null) user.get().setUserName(request.getUserName());
        if (request.getEmail() != null) user.get().setEmail(request.getEmail());
        if (request.getFirstName() != null) user.get().setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.get().setLastName(request.getLastName());
        if (request.getDepartmentId() != null)
            user.get().setDepartmentId(request.getDepartmentId() == -1 ? null : request.getDepartmentId());
        if (request.getPassword() != null && !request.getPassword().isEmpty())
            user.get().setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getSemester() != null) user.get().setSemester(request.getSemester());
        if (request.getIndexNumber() != null) user.get().setIndexNumber(request.getIndexNumber());
        if (request.getStudentBatch() != null) user.get().setBatch(request.getStudentBatch());
        userRepository.save(user.get());
        return true;
    }

    public boolean adminUpdateUser(UpdateUserRequest request) {
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("User id cannot be null.");
        }
        Optional<User> user = userRepository.findById(request.getUserId());
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found for this id.");
        }

        if (request.getEmail() != null) {
            if (!request.getEmail().equals(user.get().getEmail())) {
                Optional<User> existUserByEmail = userRepository.findByEmail(request.getEmail());
                if (existUserByEmail.isPresent())
                    throw new IllegalArgumentException("The email address already exist.");
            }
        }

        if (request.getUserName() != null) {
            if (!request.getUserName().equals(user.get().getUsername())) {
                Optional<User> existUserByUserName = userRepository.findByUserName(request.getUserName());
                if (existUserByUserName.isPresent()) throw new IllegalArgumentException("Username already exist.");
            }
        }

        if (user.get().getIndexNumber() != null) {
            assert request.getIndexNumber() != null;
            if (!request.getIndexNumber().equals(user.get().getIndexNumber())) {
                Optional<User> existUserByUserName = userRepository.findByIndexNumber(request.getIndexNumber());
                if (existUserByUserName.isPresent())
                    throw new IllegalArgumentException("This student index number is already exist.");
            }
        }

        if (request.getUserName() != null) user.get().setUserName(request.getUserName());
        if (request.getEmail() != null) user.get().setEmail(request.getEmail());
        if (request.getFirstName() != null) user.get().setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.get().setLastName(request.getLastName());
        if (request.getDepartmentId() != null)
            user.get().setDepartmentId(request.getDepartmentId() == -1 ? null : request.getDepartmentId());
        if (request.getPassword() != null && !request.getPassword().isEmpty())
            user.get().setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getSemester() != null) user.get().setSemester(request.getSemester());
        if (request.getIndexNumber() != null) user.get().setIndexNumber(request.getIndexNumber());
        if (request.getStudentBatch() != null) user.get().setBatch(request.getStudentBatch());
        userRepository.save(user.get());
        return true;
    }

    public Boolean passwordVerification(String userName, String password) {
        Optional<User> user = userRepository.findByUserName(userName);
        if (user.isPresent()) {
            return passwordEncoder.matches(password, user.get().getPassword());
        } else {
            throw new IllegalArgumentException("Requested UserName has not exist.");
        }
    }

    public Boolean forgotPasswordSendEmailForStudent(String email) {
        Optional<User> existUser = userRepository.findByEmail(email);
        if (existUser.isEmpty()) throw new IllegalArgumentException("No User Found For This Email " + email);
        if (!existUser.get().getRole().equals(Role.STUDENT)) {
            throw new IllegalArgumentException("No Student Found For This Email " + email);
        }

        return otpVerificationService.sendOTP(email);
    }

    public Boolean forgotPasswordOtpVerificationForStudent(String email, String otp) {
        Optional<User> existUser = userRepository.findByEmail(email);
        if (existUser.isEmpty()) throw new IllegalArgumentException("No User Found For This Email " + email);
        if (!existUser.get().getRole().equals(Role.STUDENT)) {
            throw new IllegalArgumentException("No Student Found For This Email " + email);
        }

        return otpVerificationService.otpVerification(email, otp);
    }

    public Boolean forgotPasswordResetPasswordForStudent(String email, String password, String userName) {
        if (userName != null) {
            Optional<User> checkUser = userRepository.findByUserName(userName);
            if (checkUser.isPresent()) {
                if (!checkUser.get().getEmail().equals(email)) {
                    throw new IllegalArgumentException("The username already taken so please change the username.");
                }
            }
        }
        Optional<User> existUser = userRepository.findByEmail(email);

        if (existUser.isEmpty()) throw new IllegalArgumentException("No Student Found For This Email " + email);
        if (!existUser.get().getRole().equals(Role.STUDENT)) {
            throw new IllegalArgumentException("No Student Found For This Email " + email);
        }

        if (userName != null) existUser.get().setUserName(userName);
        existUser.get().setPassword(passwordEncoder.encode(password));
        userRepository.save(existUser.get());
        return true;
    }

}
