package com.qrbats.qrbats.authentication.entities.user.repository;

import com.qrbats.qrbats.authentication.entities.user.Role;
import com.qrbats.qrbats.authentication.entities.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserRepositoryTest {
    @Autowired
    private UserRepository underTestUserRepo;

    private User addSampleUser(){
        User user = new User();

        user.setEmail("testemail@gmail.com");
        user.setFirstName("testUserFN");
        user.setLastName("testUserLN");
        user.setRole(Role.LECTURER);
        user.setUserName("test");
        user.setPassword(new BCryptPasswordEncoder().encode("test"));
        return underTestUserRepo.save(user);
    }

    private void deleteSameUser(User user){
        underTestUserRepo.delete(user);
    }
    @Test
    void findByUserName() {
        // given
        User sampleUser = addSampleUser();
        String userName = "test";

        // when
        Optional<User> expectedUser = underTestUserRepo.findByUserName(userName);

        // then
        assertThat(expectedUser.isPresent()).isTrue();
        assertThat(expectedUser.get()).isEqualTo(sampleUser);

        deleteSameUser(sampleUser);
    }

    @Test
    void findByRole() {
        // given
        Role userRole = Role.ADMIN;

        // when
        User expectedAdminUser = underTestUserRepo.findByRole(userRole);

        // then
        assertThat(expectedAdminUser.getRole()).isEqualTo(userRole);
    }
}