package com.qrbats.qrbats;

import com.qrbats.qrbats.authentication.entities.user.Role;
import com.qrbats.qrbats.authentication.entities.user.User;
import com.qrbats.qrbats.authentication.entities.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class QrbatsApplication implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(QrbatsApplication.class, args);
	}

	public void run(String... args){
		User adminAccount =userRepository.findByRole(Role.ADMIN);

		if(null == adminAccount){
			User user = new User();

			user.setEmail("admin@gmail.com");
			user.setFirstName("admin");
			user.setLastName("admin");
			user.setRole(Role.ADMIN);
			user.setUserName("admin");
			user.setPassword(new BCryptPasswordEncoder().encode("admin"));
			userRepository.save(user);
		}
	}

}
