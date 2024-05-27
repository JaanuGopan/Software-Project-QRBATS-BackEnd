package com.qrbats.qrbats.authentication.services.impl;

import com.qrbats.qrbats.authentication.dto.JwtAuthenticationResponse;
import com.qrbats.qrbats.authentication.dto.SignUpRequest;
import com.qrbats.qrbats.authentication.dto.SigninRequest;
import com.qrbats.qrbats.authentication.dto.UpdateUserRequest;
import com.qrbats.qrbats.authentication.entities.user.Role;
import com.qrbats.qrbats.authentication.entities.user.User;
import com.qrbats.qrbats.authentication.entities.user.repository.UserRepository;
import com.qrbats.qrbats.authentication.services.AuthenticationService;
import com.qrbats.qrbats.authentication.services.JWTService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
class AuthenticationServiceImplTest {
    @Autowired
    private AuthenticationService underTestAuthenticationService;
    @Autowired
    private UserRepository userRepository;
    @AfterEach
    void tearDown() {

    }

    private User addSampleUser(){
        User user = new User();

        user.setEmail("testemail@gmail.com");
        user.setFirstName("testUserFN");
        user.setLastName("testUserLN");
        user.setRole(Role.LECTURER);
        user.setUserName("test");
        user.setPassword(new BCryptPasswordEncoder().encode("test"));
        return user;
    }

    private void deleteSampleUser(User user){
        userRepository.delete(user);
    }

    @Test
    void canSignup() {
        // given
        User sampleUser = addSampleUser();
        String userName = "test";

        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setFirstName(sampleUser.getFirstName());
        signUpRequest.setLastName(sampleUser.getLastName());
        signUpRequest.setEmail(sampleUser.getEmail());
        signUpRequest.setUserName(sampleUser.getUsername());
        signUpRequest.setPassword("test");
        signUpRequest.setDepartmentId(sampleUser.getDepartmentId());

        // when
        User expectedUser = underTestAuthenticationService.signup(signUpRequest);

        // then
        assertThat(expectedUser).isNotNull();
        assertThat(expectedUser.getUsername()).isEqualTo(userName);

        deleteSampleUser(expectedUser);

    }

    @Test
    void testLoginWithValidCredentials() {
        // given
        String userName = "test";
        String password = "test";
        User sampleUser = userRepository.save(addSampleUser());
        SigninRequest signinRequest = new SigninRequest();
        signinRequest.setUserName(userName);
        signinRequest.setPassword(password);

        // when
        JwtAuthenticationResponse expectedResponse = underTestAuthenticationService.signin(userName,password);
        deleteSampleUser(sampleUser);

        //then
        String expectedUserName = null;
        if(expectedResponse != null){
            JWTService jwtService = new JWTServiceImpl();
            expectedUserName = jwtService.extractUserName(expectedResponse.getToken());
        }

        assertThat(expectedResponse).isNotNull();
        assertThat(expectedUserName).isEqualTo(userName);


    }

    @Test
    void testLoginWithInvalidUsername(){
        // given
        String userName = "invalid";
        String password = "test";
        User sampleUser = userRepository.save(addSampleUser());
        SigninRequest signinRequest = new SigninRequest();
        signinRequest.setUserName(userName);
        signinRequest.setPassword(password);

        // then
        assertThrows(BadCredentialsException.class, () -> {
            underTestAuthenticationService.signin(userName,password);
        });

        deleteSampleUser(sampleUser);
    }

    @Test
    void testLoginWithInvalidPassword(){
        // given
        String userName = "test";
        String password = "invalid";
        User sampleUser = userRepository.save(addSampleUser());
        SigninRequest signinRequest = new SigninRequest();
        signinRequest.setUserName(userName);
        signinRequest.setPassword(password);

        // then
        assertThrows(BadCredentialsException.class, () -> {
            underTestAuthenticationService.signin(userName,password);
        });

        deleteSampleUser(sampleUser);
    }

    @Test
    void testLoginWithNullUsername(){
        // given
        String userName = null;
        String password = "test";
        User sampleUser = userRepository.save(addSampleUser());
        SigninRequest signinRequest = new SigninRequest();
        signinRequest.setUserName(userName);
        signinRequest.setPassword(password);

        // then
        assertThrows(BadCredentialsException.class, () -> {
            underTestAuthenticationService.signin(userName,password);
        });

        deleteSampleUser(sampleUser);
    }

    @Test
    void testLoginWithNullPassword(){
        // given
        String userName = "test";
        String password = null;
        User sampleUser = userRepository.save(addSampleUser());
        SigninRequest signinRequest = new SigninRequest();
        signinRequest.setUserName(userName);
        signinRequest.setPassword(password);

        // then
        assertThrows(BadCredentialsException.class, () -> {
            underTestAuthenticationService.signin(userName,password);
        });

        deleteSampleUser(sampleUser);
    }

    @Test
    void refreshToken() {

    }

    @Test
    void getAllStaffsTest() {
        // given
        List<User> userList = userRepository.findAll();
        userList.removeIf(user -> user.getRole()==Role.ADMIN);

        // when
        List<User> expectedStaffList = underTestAuthenticationService.getAllStaffs();

        // then
        assertThat(expectedStaffList).isEqualTo(userList);
    }

    @Test
    void deleteByUserId() {
        // given
        User sampleUser = userRepository.save(addSampleUser());
        Integer userId = sampleUser.getUserId();

        // when
        underTestAuthenticationService.deleteByUserId(userId);

        // then
        assertThat(userRepository.findById(userId).isPresent()).isFalse();
    }

    @Test
    void updateUserTest() {
        // given
        User sampleUser = userRepository.save(addSampleUser());
        User updatedUser = sampleUser;
        String updatedFirstName = "updatedTestFN";
        updatedUser.setFirstName(updatedFirstName);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setUserName(updatedUser.getUsername());
        updateUserRequest.setUserId(updatedUser.getUserId());
        updateUserRequest.setEmail(updatedUser.getEmail());
        updateUserRequest.setFirstName(updatedUser.getFirstName());
        updateUserRequest.setLastName(updatedUser.getLastName());
        updateUserRequest.setDepartmentId(updatedUser.getDepartmentId());


        // when
        underTestAuthenticationService.updateUser(updateUserRequest);

        // then
        assertThat(userRepository.findById(updatedUser.getUserId()).get()).isEqualTo(updatedUser);

        deleteSampleUser(updatedUser);
    }
}