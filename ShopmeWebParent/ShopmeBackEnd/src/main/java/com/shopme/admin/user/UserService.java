package com.shopme.admin.user;

import java.util.List;
import java.util.NoSuchElementException;

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

		if (user.getId() != null) {
			// Updating an existing User.
			User exitingUser = repo.findById(user.getId()).get();
			if (user.getPassword() == null) {
				// if the password is null, set to previous password.
				user.setPassword(exitingUser.getPassword());
			} else {
				encodeUserPassword(user);
			}
		} else {
			encodeUserPassword(user);
		}
		repo.save(user);

	}

	private void encodeUserPassword(User user) {
		String encodedPassword = encoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
	}

	// Under Review
	public boolean isEmailUnique(Integer id, String email) {
		boolean isCreatingNew = (id == null);
		User userByEmail = repo.getUserByEmail(email);
		if (userByEmail == null)
			return true;
		if (isCreatingNew) {
			if (userByEmail != null)
				return false;
		} else {
			if (userByEmail.getId() != id) {
				return false;
			}
		}

		return true;
	}

	public User getUser(Integer id) throws UserNotFoundExcpetion {
		// TODO Auto-generated method stub
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new UserNotFoundExcpetion("could not find any user with the id" + id);
		}
	}

	public void deleteUser(Integer id) {
		repo.delete(repo.findById(id).get());

	}
}
