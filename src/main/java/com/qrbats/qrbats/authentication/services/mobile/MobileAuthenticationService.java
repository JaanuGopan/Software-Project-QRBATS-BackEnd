package com.qrbats.qrbats.authentication.services.mobile;

import com.qrbats.qrbats.authentication.dto.mobile.StudentSigninRequest;
import com.qrbats.qrbats.authentication.dto.JwtAuthenticationResponse;
import com.qrbats.qrbats.authentication.dto.RefreshTokenRequest;
import com.qrbats.qrbats.authentication.dto.mobile.StudentSignUpRequest;
import com.qrbats.qrbats.authentication.entities.student.Student;

import java.util.List;

public interface MobileAuthenticationService {
    Student signup(StudentSignUpRequest studentSignUpRequest);
    boolean checkStudentIsExist(String email);
    boolean checkIndexNoIsExist(String indexNo);
    boolean checkUserNameIsExist(String userName);
    JwtAuthenticationResponse signin(StudentSigninRequest studentSigninRequest);

    JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

    List<Student> getAllStudent();
}
