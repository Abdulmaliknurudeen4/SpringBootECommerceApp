package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.shopme.entity.Role;
import com.shopme.entity.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepo;
	@Autowired
	private TestEntityManager entity;

	@Test
	public void testCreateNewUserWithSingleRole() {
		Role roleAdmin = entity.find(Role.class, 1);
		User nurudeenMalik = new User("abdulmaliknurudeen4@gmail.com", "password360444", "Nurudeen", "Abdulmalik");
		nurudeenMalik.addRole(roleAdmin);

		User savedUser = userRepo.save(nurudeenMalik);
		assertThat(savedUser.getId()).isGreaterThan(0);
	}

	@Test
	public void testCreateNewUserWithMultipeRoles() {
		User nurudeenBakr = new User("abdulmaliknurudeen5@gmail.com", "password360444", "Nurudeen", "Abubakr");
		nurudeenBakr.addRole(new Role(3)); // for Editor
		nurudeenBakr.addRole(new Role(5)); // for Shipper

		User savedUser = userRepo.save(nurudeenBakr);
		assertThat(savedUser.getId()).isGreaterThan(0);
	}

	@Test
	public void testListAllUsers() {
		Iterable<User> users = userRepo.findAll();
		users.forEach(user -> System.out.println(user));
		assertThat(users.iterator()).isNotNull();
	}

	@Test
	public void testFindUserById() {
		User user = userRepo.findById(1).get();
		assertThat(user.getLastName()).isEqualTo("Abdulmalik");

	}

	@Test
	public void testUpdateUserDetails() {
		User user = userRepo.findById(1).get();
		user.setEnabled(true);
		user.setPassword("Changed");

		User savedUser = userRepo.save(user);
		assertThat(savedUser.getPassword()).isEqualTo("Changed");

	}

	@Test
	public void testUserUpdateRoles() {
		User user = userRepo.findById(3).get();
		user.getRoles().remove(new Role(3));
		user.getRoles().add(new Role(2));

		User savedUser = userRepo.save(user);
		assertThat(savedUser.getRoles()).contains(new Role(2));

	}

	@Test
	public void deleteUserById() {
		Integer userId = 2;
		userRepo.deleteById(userId);
		// This is an Error, Gotta find a way to test the deleted User.
	}

	@Test
	public void testGetUserbyEmail() {
		String email = "solder@thymeleaf.org";
		assertThat(userRepo.getUserByEmail(email).getEmail()).isEqualTo(email);
	}

	@Test
	public void testCountById() {
		Integer id = 52;
		assertThat(userRepo.countById(52)).isGreaterThan(0);
	}

	@Test
	public void testEnableUserStatus() {
		Integer id = 52;
		userRepo.EnableStatusUser(id, true);
		assertThat(userRepo.findById(id).get().isEnabled()).isTrue();
	}
	
	@Test
	public void testListFirstPage() {
		int pageNumber = 0;
		int pageSize = 4;
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = userRepo.findAll(pageable);
		List<User> listUser = page.getContent();
		
		listUser.forEach(user->System.out.println(user));
		assertThat(listUser.size()).isEqualTo(pageSize);
	}

	@Test
	public void testSearchUsers(){
		String keyword = "bruce";

		int pageNumber = 0;
		int pageSize = 4;
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = userRepo.findAll(keyword, pageable);
		List<User> listUser = page.getContent();

		listUser.forEach(user->System.out.println(user));
		assertThat(listUser.size()).isGreaterThan(0);

	}

}
