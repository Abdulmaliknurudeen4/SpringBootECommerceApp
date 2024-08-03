package com.shopme.address;

import com.shopme.entity.Address;
import com.shopme.entity.Customer;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AddressRepository extends CrudRepository<Address, Integer> {

    List<Address> findByCustomer(Customer customer);

    @Query("SELECT a FROM Address a WHERE a.Id = ?1 AND a.customer.id = ?2")
    Address findByIdAndCustomer(Integer addressId, Integer customerId);

    @Query("DELETE FROM Address a WHERE a.Id = ?1 AND a.customer.id = ?2")
    @Modifying
    void deleteByIdAndCustomer(Integer addressId, Integer customerId);

    @Query("UPDATE Address a SET a.defaultForShipping = true WHERE a.Id = ?1")
    @Modifying
    void setDefaultAddress(Integer id);

    @Query("UPDATE Address a SET a.defaultForShipping = false WHERE a.Id <> ?1 AND a.customer.id = ?2")
    @Modifying
    void setNonDefaultForOthers(Integer defaultAddressId, Integer customerId);
}
