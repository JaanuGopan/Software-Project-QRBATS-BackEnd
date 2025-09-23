package com.qrbats.qrbats.authentication.controller;

import com.qrbats.qrbats.authentication.dto.*;
import com.qrbats.qrbats.authentication.entities.user.User;
import com.qrbats.qrbats.authentication.services.AuthenticationService;
import com.qrbats.qrbats.authentication.services.UserService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody SignUpRequest signUpRequest){
        try {
            return ResponseEntity.ok(authenticationService.signup(signUpRequest));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/admin-creates-user")
    public ResponseEntity<User> adminCreatesUser(@RequestBody SignUpRequest signUpRequest){
        try {
            return ResponseEntity.ok(authenticationService.adminCreatesUser(signUpRequest));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> staffLogin(@RequestParam String userName,@RequestParam String password){
        try {
            return ResponseEntity.ok(authenticationService.staffLogin(userName,password));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/student-login")
    public ResponseEntity<?> studentLogin(@RequestParam String userName, @RequestParam String password, @RequestParam String deviceId){
        try {
            if (authenticationService.verifyDeviceForStudent(userName,password,deviceId)){
                JwtAuthenticationResponse jwtAuthRes = authenticationService.studentLogin(userName, password, deviceId);
                return ResponseEntity.ok(jwtAuthRes);
            }else {
                Map<String, String> response = new HashMap<>();
                response.put("error", "DEVICE_VERIFICATION_FAILED");
                response.put("message", "This account is not registered for this device. Please use the registered device or verify your new device.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/send-otp-for-device-verification")
    public ResponseEntity<Boolean> sendOtpForDeviceVerification(@RequestParam String email, @RequestParam String username, @RequestParam String password, @RequestParam String deviceId) {
        try {
            return ResponseEntity.ok(authenticationService.sendOtpForDeviceVerification(email,username,password,deviceId));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/change-device-otp-verification")
    public ResponseEntity<Boolean> changeDeviceOtpVerification(@RequestParam String email, @RequestParam String otp, @RequestParam String deviceId){
        try {
            return ResponseEntity.ok(authenticationService.otpVerificationForDeviceVerification(email, otp, deviceId));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAuthenticatedUser() {
        User user = userService.getAuthenticatedUser();

        if (user != null) {
            return ResponseEntity.ok(new UserResponseDTO(
                    user.getUserId(),
                    user.getUsername(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getDepartmentId(),
                    user.getSemester(),
                    user.getIndexNumber(),
                    user.getRole(),
                    user.getBatch()
            ));
        } else {
            return ResponseEntity.status(401).body("User not authenticated");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationResponse> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest){
        return ResponseEntity.ok(authenticationService.refreshToken(refreshTokenRequest));
    }

    @PutMapping("/update-user")
    public ResponseEntity<Boolean> updateUser(@RequestBody UpdateUserRequest request){
        try {
             return ResponseEntity.ok(authenticationService.updateUser(request));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/admin-update-user")
    public ResponseEntity<Boolean> adminUpdateUser(@RequestBody UpdateUserRequest request){
        try {
            return ResponseEntity.ok(authenticationService.adminUpdateUser(request));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/update-student")
    public ResponseEntity<Boolean> updateStudent(@RequestBody UpdateUserRequest request){
        try {
             return ResponseEntity.ok(authenticationService.updateUser(request));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/verify-password")
    public ResponseEntity<Boolean> verifyPassword(@RequestParam String userName,@RequestParam String password){
        return ResponseEntity.ok(authenticationService.passwordVerification(userName,password));
    }

    @GetMapping("/verify-password-profile-edit")
    public ResponseEntity<Boolean> verifyPasswordForProfileEdit(@RequestParam String userName,@RequestParam String password){
        try {
            return ResponseEntity.ok(authenticationService.passwordVerificationForProfileEdit(userName,password));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/forgot-password-send-otp")
    public ResponseEntity<Boolean> forgotPasswordSendEmail(@RequestParam String email){
        try {
            return ResponseEntity.ok(authenticationService.forgotPasswordSendEmail(email));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/forgot-password-verify-otp")
    public ResponseEntity<Boolean> forgotPasswordOtpVerification(@RequestParam String email ,@RequestParam String otp){
        try {
            return ResponseEntity.ok(authenticationService.forgotPasswordOtpVerification(email,otp));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/forgot-password-reset-password")
    public ResponseEntity<Boolean> forgotPasswordResetPassword(@RequestParam String email ,@RequestParam String password,@RequestParam @Nullable String userName){
        try {
            return ResponseEntity.ok(authenticationService.forgotPasswordResetPassword(email,password,userName));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/forgot-password-send-otp-for-student")
    public ResponseEntity<Boolean> forgotPasswordSendEmailForStudent(@RequestParam String email){
        try {
            return ResponseEntity.ok(authenticationService.forgotPasswordSendEmailForStudent(email));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/forgot-password-verify-otp-for-student")
    public ResponseEntity<Boolean> forgotPasswordOtpVerificationForStudent(@RequestParam String email ,@RequestParam String otp){
        try {
            return ResponseEntity.ok(authenticationService.forgotPasswordOtpVerificationForStudent(email,otp));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/forgot-password-reset-password-for-student")
    public ResponseEntity<Boolean> forgotPasswordResetPasswordForStudent(@RequestParam String email ,@RequestParam String password,@RequestParam @Nullable String userName){
        try {
            return ResponseEntity.ok(authenticationService.forgotPasswordResetPasswordForStudent(email,password,userName));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/check-student-signup-details")
    public ResponseEntity<Boolean> checkStudentSignupDetails(@RequestParam String email, @RequestParam String indexNumber, @RequestParam String deviceId){
        try {
            return ResponseEntity.ok(authenticationService.checkStudentSignupDetails(email,indexNumber,deviceId));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
