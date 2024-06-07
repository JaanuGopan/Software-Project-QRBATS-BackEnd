package com.qrbats.qrbats.authentication.controller.mobile;

import com.qrbats.qrbats.authentication.dto.JwtAuthenticationResponse;
import com.qrbats.qrbats.authentication.dto.RefreshTokenRequest;
import com.qrbats.qrbats.authentication.dto.mobile.*;
import com.qrbats.qrbats.authentication.entities.student.Student;
import com.qrbats.qrbats.authentication.services.mobile.MobileAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mobile")
@RequiredArgsConstructor
@CrossOrigin("*")
public class StudentAuthenticationController {

    private final MobileAuthenticationService mobileAuthenticationService;

    @PostMapping("/signup")
    public ResponseEntity<Student> signup(@RequestBody StudentSignUpRequest signUpRequest){
        return ResponseEntity.ok(mobileAuthenticationService.signup(signUpRequest));
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationResponse> signin(@RequestBody StudentSigninRequest signinRequest){
        return ResponseEntity.ok(mobileAuthenticationService.signIn(signinRequest));
    }

    @PostMapping("/checkstudentemail")
    public ResponseEntity<Boolean> checkStudentEmailIsExist(@RequestBody StudentCheckStudentEmailRequest studentCheckStudentEmailRequest){
        return ResponseEntity.ok(mobileAuthenticationService.checkStudentIsExist(studentCheckStudentEmailRequest.getStudentEmail()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationResponse> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest){
        return ResponseEntity.ok(mobileAuthenticationService.refreshToken(refreshTokenRequest));
    }

    @PostMapping("/getallstudents")
    public ResponseEntity<List<Student>> getAllStudents(){
        return ResponseEntity.ok(mobileAuthenticationService.getAllStudent());
    }


    @PostMapping("/checkstudentindexno")
    public ResponseEntity<Boolean> checkStudentIndexNoIsExist(@RequestBody StudentCheckStudentIndexNoRequest studentCheckStudentIndexNoRequest){
        return ResponseEntity.ok(mobileAuthenticationService.checkIndexNoIsExist(studentCheckStudentIndexNoRequest.getStudentIndexNo()));
    }

    @PostMapping("/checkstudentusername")
    public ResponseEntity<Boolean> checkStudentUserNameIsExist(@RequestBody StudentCheckUserNameRequest studentCheckUserNameRequest){
        return ResponseEntity.ok(mobileAuthenticationService.checkUserNameIsExist(studentCheckUserNameRequest.getStudentUserName()));
    }

    @PutMapping("/updatestudent")
    public ResponseEntity<?> updateStudent(@RequestBody StudentUpdateRequest studentUpdateRequest){
        try {
           return ResponseEntity.ok(mobileAuthenticationService.updateStudentDetails(studentUpdateRequest));
        } catch (RuntimeException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/createstudentbyadmin")
    public ResponseEntity<?> createStudentByAdmin(@RequestBody AdminCreateStudentRequest request){
        try {
            return ResponseEntity.ok(mobileAuthenticationService.adminCreateStudent(request));
        } catch (RuntimeException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/deletestudentbystudentid/{studentId}")
    public ResponseEntity<?> deleteStudentByStudentId(@PathVariable Integer studentId){
        try {
            return ResponseEntity.ok(mobileAuthenticationService.deleteStudent(studentId));
        }catch (RuntimeException ex){
            return  ResponseEntity.badRequest().body(ex.getMessage());
        }
    }


}
