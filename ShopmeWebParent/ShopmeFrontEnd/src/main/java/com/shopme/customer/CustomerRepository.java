package com.shopme.customer;

import com.shopme.entity.AuthenticationType;
import com.shopme.entity.Customer;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Integer> {
    Customer findByEmail(String email);

    Customer findByVerficationCode(String verificationCode);

    @Query("UPDATE Customer c SET c.enabled = true, c.verficationCode = null WHERE c.id = ?1")
    @Modifying
    void enable(Integer customer_id);

    @Query("UPDATE Customer c SET c.authenticationType = ?2 WHERE c.id = ?1")
    @Modifying
    void updateAuthenticationType(Integer Id, AuthenticationType authenticationType);

}
