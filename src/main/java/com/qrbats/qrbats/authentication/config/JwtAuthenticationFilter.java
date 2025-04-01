package com.qrbats.qrbats.authentication.config;

import com.qrbats.qrbats.authentication.services.UserService;
import com.qrbats.qrbats.authentication.services.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserService userService;

    // List of endpoints to be excluded (supports wildcard matching)
    private static final List<RequestMatcher> EXCLUDED_URLS = List.of(
            new AntPathRequestMatcher("/api/v1/auth/signup/**"),
            new AntPathRequestMatcher("/api/v1/auth/login/**"),
            new AntPathRequestMatcher("/api/v1/auth/student-login/**"),
            new AntPathRequestMatcher("/api/v1/auth/verify-password/**"),
            new AntPathRequestMatcher("/api/v1/auth/forgot-password-send-otp-for-student/**"),
            new AntPathRequestMatcher("/api/v1/auth/forgot-password-verify-otp-for-student/**"),
            new AntPathRequestMatcher("/api/v1/auth/forgot-password-reset-password-for-student/**"),
            new AntPathRequestMatcher("/api/v1/auth/check-student-email/**"),
            new AntPathRequestMatcher("/api/v1/auth/check-student-index-number/**"),
            new AntPathRequestMatcher("/api/v1/auth/check-student-username/**"),
            new AntPathRequestMatcher("/api/v1/otp/**")
    );

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Skip JWT authentication for excluded URLs using Ant-style matching
        if (EXCLUDED_URLS.stream().anyMatch(matcher -> matcher.matches(request))) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userName;

        if (StringUtils.isEmpty(authHeader) || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        userName = jwtService.extractUserName(jwt);

        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userService.userDetailsService().loadUserByUsername(userName);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                securityContext.setAuthentication(token);
                SecurityContextHolder.setContext(securityContext);
            }
        }

        filterChain.doFilter(request, response);
    }
}
