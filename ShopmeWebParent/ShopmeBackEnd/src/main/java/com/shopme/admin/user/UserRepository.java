package com.shopme.admin.user;

import com.shopme.admin.paging.SearchRepository;
import com.shopme.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends SearchRepository<User, Integer> {

    @Query("SELECT u from User u WHERE u.email = :email")
    User getUserByEmail(@Param("email") String email);

    Long countById(Integer Id);

    @Query("SELECT u FROM User u WHERE CONCAT(u.id, ' ', u.email, ' ', u.firstName, ' ', u.lastName) LIKE %?1%")
    Page<User> findAll(String keyword, Pageable pageable);

    @Query("UPDATE User u SET u.enabled = ?2 WHERE u.id = ?1")
    @Modifying
    void EnableStatusUser(Integer id, boolean status);

}
