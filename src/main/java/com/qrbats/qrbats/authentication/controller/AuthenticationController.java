package com.qrbats.qrbats.authentication.controller;

import com.qrbats.qrbats.authentication.dto.*;
import com.qrbats.qrbats.authentication.entities.user.User;
import com.qrbats.qrbats.authentication.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody SignUpRequest signUpRequest){
        return ResponseEntity.ok(authenticationService.signup(signUpRequest));
    }

    @GetMapping("/signin")
    public ResponseEntity<?> signIn(@RequestParam String userName,@RequestParam String password){
        try {
            return ResponseEntity.ok(authenticationService.signin(userName,password));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationResponse> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest){
        return ResponseEntity.ok(authenticationService.refreshToken(refreshTokenRequest));
    }

    @PostMapping("/getallstaffs")
    public ResponseEntity<List<User>> getAllStaffs(){
       return ResponseEntity.ok(authenticationService.getAllStaffs());
    }

    @PostMapping("/deleteuserbyuserid")
    public void deleteUserById(@RequestBody DeleteUserByIdRequest deleteUserByIdRequest){
        authenticationService.deleteByUserId(deleteUserByIdRequest.getUserId());
    }

    @PutMapping("/updateuser")
    public void updateUser(@RequestBody UpdateUserRequest request){
        authenticationService.updateUser(request);
    }

    @GetMapping("/verifypassword")
    public ResponseEntity<Boolean> verifyPassword(@RequestParam String userName,@RequestParam String password){
        return ResponseEntity.ok(authenticationService.passwordVerification(userName,password));
    }
}
