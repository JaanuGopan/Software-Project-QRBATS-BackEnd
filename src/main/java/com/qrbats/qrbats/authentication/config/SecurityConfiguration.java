package com.qrbats.qrbats.authentication.config;

import com.qrbats.qrbats.authentication.entities.user.Role;
import com.qrbats.qrbats.authentication.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final UserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.csrf(AbstractHttpConfigurer :: disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/api/v1/auth/**","/swagger-ui.html").permitAll()
                        .requestMatchers("api/v1/admin/**").hasAnyAuthority(Role.ADMIN.name())
                        .requestMatchers("api/v1/lecturer/**").hasAnyAuthority(Role.LECTURER.name())
                        .requestMatchers("api/v1/student/**").hasAnyAuthority(Role.STUDENT.name())
                        .requestMatchers("api/v1/mobile/**").permitAll()
                        .requestMatchers("api/v1/attendance/**").permitAll()
                        .requestMatchers("api/v1/module/**").permitAll()
                        .requestMatchers("api/v1/event/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/api/v1/location/**").permitAll()
                        .requestMatchers("/api/v1/otp/**").permitAll()
                        .requestMatchers("api/v1/report/**").permitAll()
                        .requestMatchers("api/v1/lecture/**").permitAll()
                        .requestMatchers("api/v1/lectureattendance/**").permitAll()
                        .requestMatchers("api/v1/export/**").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider()).addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class
                );
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userService.userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }

}
