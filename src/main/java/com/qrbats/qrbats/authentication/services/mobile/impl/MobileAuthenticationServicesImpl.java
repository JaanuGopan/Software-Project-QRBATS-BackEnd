package com.qrbats.qrbats.authentication.services.mobile.impl;

import com.qrbats.qrbats.authentication.dto.JwtAuthenticationResponse;
import com.qrbats.qrbats.authentication.dto.RefreshTokenRequest;
import com.qrbats.qrbats.authentication.dto.mobile.StudentSignUpRequest;
import com.qrbats.qrbats.authentication.dto.mobile.StudentSigninRequest;
import com.qrbats.qrbats.authentication.entities.student.Student;
import com.qrbats.qrbats.authentication.entities.student.StudentRole;
import com.qrbats.qrbats.authentication.entities.student.repository.StudentRepository;
import com.qrbats.qrbats.authentication.services.JWTService;
import com.qrbats.qrbats.authentication.services.mobile.MobileAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class MobileAuthenticationServicesImpl implements MobileAuthenticationService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    @Override
    public Student signup(StudentSignUpRequest studentSignUpRequest) {
        Student student = new Student();
        student.setStudentEmail(studentSignUpRequest.getStudentEmail());
        student.setStudentName(studentSignUpRequest.getStudentName());
        student.setIndexNumber(studentSignUpRequest.getIndexNumber());
        student.setDepartmentId(studentSignUpRequest.getDepartmentId());
        student.setStudentRole(StudentRole.UORSTUDENT);
        student.setCurrentSemester(studentSignUpRequest.getCurrentSemester());
        student.setUserName(studentSignUpRequest.getUserName());
        student.setPassword(passwordEncoder.encode(studentSignUpRequest.getPassword()));

        return studentRepository.save(student);

    }

    @Override
    public JwtAuthenticationResponse signin(StudentSigninRequest studentSigninRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        studentSigninRequest.getUserName(),
                        studentSigninRequest.getPassword()
                )
        );

        var student = studentRepository.findByUserName(
                studentSigninRequest.getUserName()).orElseThrow(
                () -> new IllegalArgumentException("Invalid userName or password")
        );

        var jwt = jwtService.generateToken(student);
        var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), student);

        JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();

        jwtAuthenticationResponse.setToken(jwt);
        jwtAuthenticationResponse.setRefreshToken(refreshToken);
        return jwtAuthenticationResponse;
    }

    @Override
    public JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String userName = jwtService.extractUserName(refreshTokenRequest.getToken());
        Student student = studentRepository.findByUserName(userName).orElseThrow();

        if(jwtService.isTokenValid(refreshTokenRequest.getToken(),student)){
            var jwt = jwtService.generateToken(student);

            JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();

            jwtAuthenticationResponse.setToken(jwt);
            jwtAuthenticationResponse.setRefreshToken(refreshTokenRequest.getToken());
            return jwtAuthenticationResponse;

        }
        return null;
    }
}
