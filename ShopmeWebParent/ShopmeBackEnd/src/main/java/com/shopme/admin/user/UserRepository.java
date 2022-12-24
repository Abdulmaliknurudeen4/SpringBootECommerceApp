package com.shopme.admin.user;

import org.springframework.data.repository.CrudRepository;

import com.shopme.entity.User;

public interface UserRepository extends CrudRepository<User, Integer> {

}
