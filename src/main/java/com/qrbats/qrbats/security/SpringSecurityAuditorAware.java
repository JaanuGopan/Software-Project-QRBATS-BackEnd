package com.qrbats.qrbats.security;

import com.qrbats.qrbats.authentication.entities.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.Optional;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<Integer> {

    @Override
    public Optional<Integer> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof User user) {
                Integer userId = user.getUserId();

                return Optional.ofNullable(userId);
            }
        }

        return Optional.empty();
    }
}
