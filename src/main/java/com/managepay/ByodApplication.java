package com.managepay;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ByodApplication implements CommandLineRunner {
	
	public static void main(String[] args) {
		SpringApplication.run(ByodApplication.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception {
		/*System.out.println("at main");
		Role adminRole = roleRepository.save(new Role(Role.MPAY_ADMIN));
		
		User user = new User();
		user.setName("admin");
		user.setUserName("admin");
		user.setAddress("123");
		user.setContactPerson("123");
		user.setCpnRegistrationNumber("123");
		user.setEmail("123");
		user.setEnabled(true);
		user.setPassword(passwordEncoder.encode("12345678"));
		user.setPhoneNumber("123");
		user.setRoles(adminRole);
		User saved = userRepository.save(user);*/
	}
}
