package com.qrbats.qrbats;

import com.qrbats.qrbats.authentication.entities.user.Role;
import com.qrbats.qrbats.authentication.entities.user.User;
import com.qrbats.qrbats.authentication.entities.user.repository.UserRepository;
import com.qrbats.qrbats.functionalities.department.service.DepartmentService;
import com.qrbats.qrbats.functionalities.location.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
@EnableScheduling
public class QrbatsApplication implements CommandLineRunner {

	private final UserRepository userRepository;
	private final LocationService locationService;
	private final PasswordEncoder passwordEncoder;
	private final DepartmentService departmentService;

	@Value("${admin.email}")
	private String adminEmail;
	@Value("${admin.firstName}")
	private String adminFirstName;
	@Value("${admin.lastName}")
	private String adminLastName;
	@Value("${admin.username}")
	private String adminUsername;
	@Value("${admin.password}")
	private String adminPassword;


	public static void main(String[] args) {
		SpringApplication.run(QrbatsApplication.class, args);
	}

	public void run(String... args){
		List<User> adminAccounts =userRepository.findALLByRole(Role.ADMIN);

		locationService.addLocations();

		if(adminAccounts.isEmpty()){
			User user = new User();
			user.setEmail(adminEmail);
			user.setFirstName(adminFirstName);
			user.setLastName(adminLastName);
			user.setRole(Role.ADMIN);
			user.setUserName(adminUsername);
			user.setPassword(passwordEncoder.encode(adminPassword));
			userRepository.save(user);
		}

		departmentService.createDefaultDepartment();
	}

}
