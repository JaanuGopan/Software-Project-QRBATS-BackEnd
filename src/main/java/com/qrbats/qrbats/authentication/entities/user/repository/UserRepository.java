package com.qrbats.qrbats.authentication.entities.user.repository;

import com.qrbats.qrbats.authentication.entities.user.Role;
import com.qrbats.qrbats.authentication.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUserName(String email);

    User findByRole(Role role);
}
