package com.qrbats.qrbats.authentication.services;

import com.qrbats.qrbats.authentication.entities.user.Role;
import com.qrbats.qrbats.authentication.entities.user.User;
import com.qrbats.qrbats.authentication.entities.user.repository.UserRepository;
import com.qrbats.qrbats.entity.attendance.AttendanceRepository;
import com.qrbats.qrbats.entity.attendance.event.EventAttendanceRepository;
import com.qrbats.qrbats.entity.module_enrollment.ModuleEnrollmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final ModuleEnrollmentRepository moduleEnrollmentRepository;
    private final AttendanceRepository attendanceRepository;
    private final EventAttendanceRepository eventAttendanceRepository;

    public UserDetailsService userDetailsService(){
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return userRepository.findByUserName(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
            }
        };
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            return userRepository.findByUserName(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
        }

        return null;
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

    @Transactional
    public Boolean deleteByUserId(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid userId."));
        if (user.getRole() == Role.STUDENT) {
            moduleEnrollmentRepository.deleteAllByStudentId(userId);
            attendanceRepository.deleteAllByAttendeeId(userId);
            eventAttendanceRepository.deleteAllByAttendeeId(userId);
        }
        userRepository.deleteById(userId);
        return true;
    }

    public User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }
}
