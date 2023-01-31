package com.shopme.admin.user;

import com.shopme.entity.Role;
import com.shopme.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class UserService {
	
	public static final int USER_PER_PAGE = 4;
	
	@Autowired
	private UserRepository repo;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private RoleRepository roleRepo;

	public List<User> listAll() {
		return (List<User>) repo.findAll(Sort.by("firstName").ascending());
	}

	public List<Role> listRoles() {
		return (List<Role>) roleRepo.findAll();
	}
	
	public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword){
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNum-1, USER_PER_PAGE, sort);
		if(keyword!=null){
			return repo.findAll(keyword, pageable);
		}
		return repo.findAll(pageable);
	}

	public User save(User user) {

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
	return repo.save(user);

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

	public void deleteUser(Integer id) throws UserNotFoundExcpetion {
		Long userCount = repo.countById(id);
		if (userCount == null || userCount == 0) {
			throw new UserNotFoundExcpetion("User not Present !!");
		}
		repo.deleteById(id);

	}

	public void setEnableUser(boolean status, Integer id) throws UserNotFoundExcpetion {
		Long userCount = repo.countById(id);
		if (userCount == null || userCount == 0) {
			throw new UserNotFoundExcpetion("User not Present !!");
		}
		repo.EnableStatusUser(id, status);
	}

    public User getUser(String userMail) {
		return repo.getUserByEmail(userMail);
    }
}
