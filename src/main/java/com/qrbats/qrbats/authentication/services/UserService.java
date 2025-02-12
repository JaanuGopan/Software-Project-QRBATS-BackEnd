package com.qrbats.qrbats.authentication.services;

import com.qrbats.qrbats.authentication.entities.user.Role;
import com.qrbats.qrbats.authentication.entities.user.User;
import com.qrbats.qrbats.authentication.entities.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDetailsService userDetailsService(){
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return userRepository.findByUserName(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
            }
        };
    }

    public List<User> getAllStaffs() {
        List<User> userList = userRepository.findAll();
        return userList.stream()
                .filter(user -> user.getRole() == Role.LECTURER)
                .collect(Collectors.toList());
    }

    public List<User> getAllStudent() {
        List<User> userList = userRepository.findAll();
        return userList.stream().filter(user -> user.getRole().equals(Role.STUDENT)).toList();
    }
}
