package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.shopme.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.function.Consumer;
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class PasswordEncoderTest {
	@Autowired
	private UserRepository userRepo;

	@Test
	public void testEncoderPassword() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String rawPassword = "password360444";
		String encodedPassword = encoder.encode(rawPassword);
		System.out.println(encodedPassword);
		assertThat(encoder.matches(rawPassword, encodedPassword)).isTrue();
	}

	@Test
	public void changeAllUsers(){
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		Iterable<User> userList = userRepo.findAll();
		for (User me : userList) {
			me.setPassword(encoder.encode("password"));
			userRepo.save(me);
		}
		assertThat("ma".matches("ma")).isTrue();
	}

}
