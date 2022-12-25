package com.shopme.admin.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.entity.Role;
import com.shopme.entity.User;

@Service
public class UserService {
	@Autowired
	private UserRepository repo;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private RoleRepository roleRepo;

	public List<User> listAll() {
		return (List<User>) repo.findAll();
	}

	public List<Role> listRoles() {
		return (List<Role>) roleRepo.findAll();
	}

	public void save(User user) {
		encodeUserPassword(user);
		repo.save(user);

	}

	private void encodeUserPassword(User user) {
		String encodedPassword = encoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
	}
	
	public boolean isEmailUnique(String email) {
		return repo.getUserByEmail(email) ==null;
	}
}
