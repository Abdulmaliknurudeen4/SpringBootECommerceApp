package com.shopme.admin.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.shopme.entity.User;

public interface UserRepository extends CrudRepository<User, Integer> {

	@Query("SELECT u from User u WHERE u.email = :email")
	public User getUserByEmail(@Param("email") String email);

	public Long countById(Integer Id);
}
