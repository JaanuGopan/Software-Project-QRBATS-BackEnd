package com.qrbats.qrbats.authentication.controller;

import com.qrbats.qrbats.authentication.dto.*;
import com.qrbats.qrbats.authentication.entities.user.User;
import com.qrbats.qrbats.authentication.services.AuthenticationService;
import com.qrbats.qrbats.authentication.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignUpRequest signUpRequest){
        try {
            return ResponseEntity.ok(authenticationService.signup(signUpRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/login")
    public ResponseEntity<?> staffLogin(@RequestParam String userName,@RequestParam String password){
        try {
            return ResponseEntity.ok(authenticationService.staffLogin(userName,password));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/student-login")
    public ResponseEntity<?> studentLogin(@RequestParam String userName,@RequestParam String password){
        try {
            return ResponseEntity.ok(authenticationService.studentLogin(userName,password));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAuthenticatedUser() {
        Optional<User> userOptional = userService.getAuthenticatedUser();

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            return ResponseEntity.ok(new UserResponseDTO(
                    user.getUserId(),
                    user.getUsername(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getDepartmentId(),
                    user.getSemester(),
                    user.getIndexNumber(),
                    user.getRole()
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
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserRequest request){
        try {
             return ResponseEntity.ok(authenticationService.updateUser(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/verify-password")
    public ResponseEntity<Boolean> verifyPassword(@RequestParam String userName,@RequestParam String password){
        return ResponseEntity.ok(authenticationService.passwordVerification(userName,password));
    }

    @PostMapping("/forgot-password-send-otp")
    public ResponseEntity<?> forgotPasswordSendEmail(@RequestParam String email){
        try {
            return ResponseEntity.ok(authenticationService.forgotPasswordSendEmail(email));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/forgot-password-verify-otp")
    public ResponseEntity<?> forgotPasswordOtpVerification(@RequestParam String email ,@RequestParam String otp){
        try {
            return ResponseEntity.ok(authenticationService.forgotPasswordOtpVerification(email,otp));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/forgot-password-reset-password")
    public ResponseEntity<?> forgotPasswordResetPassword(@RequestParam String email ,@RequestParam String password,@RequestParam String userName){
        try {
            return ResponseEntity.ok(authenticationService.forgotPasswordResetPassword(email,password,userName));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/check-student-email")
    public ResponseEntity<Boolean> checkStudentEmailIsExist(@RequestParam String email){
        return ResponseEntity.ok(authenticationService.checkStudentIsExistByEmail(email));
    }

    @PostMapping("/check-student-index-number")
    public ResponseEntity<Boolean> checkStudentIndexNoIsExist(@RequestParam String indexNumber){
        return ResponseEntity.ok(authenticationService.checkStudentIndexNumberIsExist(indexNumber));
    }

    @PostMapping("/check-student-username")
    public ResponseEntity<Boolean> checkStudentUserNameIsExist(@RequestParam String userName){
        return ResponseEntity.ok(authenticationService.checkStudentUserNameIsExist(userName));
    }

}
