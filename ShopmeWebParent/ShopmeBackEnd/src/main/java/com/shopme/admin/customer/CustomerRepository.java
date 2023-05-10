package com.shopme.admin.customer;

import com.shopme.admin.paging.SearchRepository;
import com.shopme.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Integer>, SearchRepository<Customer, Integer> {

    Long countById(Integer id);

    @Query("UPDATE Customer cus SET cus.enabled = ?2 WHERE cus.id = ?1")
    @Modifying
    void enableCustomerStatus(Integer id, boolean status);

    @Query("SELECT c FROM Customer c WHERE CONCAT(c.firstName, c.lastName, c.email) LIKE %?1%")
    Page<Customer> findAll(String keyword, Pageable pageable);

    Customer getCustomerByEmail(String email);


}
