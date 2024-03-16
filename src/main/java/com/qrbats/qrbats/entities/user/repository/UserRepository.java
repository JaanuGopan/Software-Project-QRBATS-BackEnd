package com.qrbats.qrbats.entities.user.repository;

import com.qrbats.qrbats.entities.user.Role;
import com.qrbats.qrbats.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUserName(String email);

    User findByRole(Role role);
}
