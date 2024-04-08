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
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MobileAuthenticationServicesImpl implements MobileAuthenticationService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    private final JWTService jwtService;
    @Override
    public Student signup(StudentSignUpRequest studentSignUpRequest) {
        if(!checkStudentIsExist(studentSignUpRequest.getStudentEmail())){
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
        }else {
            throw new RuntimeException("This Student is already exist.");
        }
    }

    @Override
    public boolean checkStudentIsExist(String email) {
        Student oldStudent = studentRepository.findByStudentEmail(email);
        System.out.println(oldStudent);
        return oldStudent != null;
    }

    @Override
    public boolean checkIndexNoIsExist(String indexNo) {
        Optional<Student> oldStudent = studentRepository.findByIndexNumber(indexNo);
        return oldStudent.isPresent();
    }

    @Override
    public boolean checkUserNameIsExist(String userName) {
        Optional<Student> oldStudent = studentRepository.findByUserName(userName);
        return oldStudent.isPresent();
    }

    @Override
    public JwtAuthenticationResponse signin(StudentSigninRequest studentSigninRequest) {
        System.out.println("qwerty");

        Optional<Student> loginStudent = studentRepository.findByUserName(studentSigninRequest.getStudentUserName());
        if (loginStudent.isPresent()){
            boolean isPasswordCorrect = passwordEncoder.matches(studentSigninRequest.getPassword(),loginStudent.get().getPassword());
            if (isPasswordCorrect){
                Map<String, Object> extraClaims = new HashMap<>();

                extraClaims.put("studentName",loginStudent.get().getStudentName());
                extraClaims.put("studentId",loginStudent.get().getStudentId());
                extraClaims.put("indexNumber",loginStudent.get().getIndexNumber());
                extraClaims.put("studentEmail", loginStudent.get().getStudentEmail());
                extraClaims.put("currentSemester",loginStudent.get().getCurrentSemester());
                extraClaims.put("departmentId",loginStudent.get().getDepartmentId());
                extraClaims.put("studentRole",loginStudent.get().getStudentRole());

                var jwt = jwtService.generateToken(loginStudent.get(), extraClaims);
                var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), loginStudent.get());

                JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();

                jwtAuthenticationResponse.setToken(jwt);
                jwtAuthenticationResponse.setRefreshToken(refreshToken);
                return jwtAuthenticationResponse;
            }else {
                throw new IllegalArgumentException("Invalid userName or password");
            }
        }else {
            throw new IllegalArgumentException("Invalid userName or password");
        }
    }

    @Override
    public JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String userName = jwtService.extractUserName(refreshTokenRequest.getToken());
        Student student = studentRepository.findByUserName(userName).orElseThrow();

        if(jwtService.isTokenValid(refreshTokenRequest.getToken(),student)){
            Map<String, Object> extraClaims = new HashMap<>();

            extraClaims.put("studentName",student.getStudentName());
            extraClaims.put("studentId",student.getStudentId());
            extraClaims.put("indexNumber",student.getIndexNumber());
            extraClaims.put("studentEmail", student.getStudentEmail());
            extraClaims.put("currentSemester",student.getCurrentSemester());
            extraClaims.put("departmentId",student.getDepartmentId());
            extraClaims.put("studentRole",student.getStudentRole());

            var jwt = jwtService.generateToken(student,extraClaims);

            JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();

            jwtAuthenticationResponse.setToken(jwt);
            jwtAuthenticationResponse.setRefreshToken(refreshTokenRequest.getToken());
            return jwtAuthenticationResponse;

        }
        return null;
    }

    @Override
    public List<Student> getAllStudent() {
        return studentRepository.findAll();
    }
}
