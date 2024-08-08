package com.shopme.address;

import com.shopme.entity.Address;
import com.shopme.entity.Country;
import com.shopme.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class AddressRepositoryTests {

    @Autowired
    private AddressRepository repo;

    @Test
    public void testAddNew() {
        Integer customerId = 5;
        Integer countryId = 234; // USA

        Address newAddress = new Address();
        newAddress.setCustomer(new Customer(customerId));
        newAddress.setCountry(new Country(countryId));
        newAddress.setFirstName("AbdulRauf");
        newAddress.setLastName("Nurudeen");
        newAddress.setPhoneNumber("+234-802-986-7835");
        newAddress.setAddressLine1("204 MorningView Lane");
        newAddress.setCity("New York");
        newAddress.setState("New York");
        newAddress.setPostalCode("10013");
        newAddress.setDefaultForShipping(true);

        Address savedAddress = repo.save(newAddress);

        assertThat(savedAddress).isNotNull();
        assertThat(savedAddress.getId()).isGreaterThan(0);

    }

    @Test
    public void testFindByCustomer() {
        Integer customerId = 5;
        List<Address> listAddresses = repo.findByCustomer(new Customer(customerId));
        assertThat(listAddresses.size()).isGreaterThan(0);

        listAddresses.forEach(System.out::println);
    }

    @Test
    public void testFindByIdAndCustomer() {
        Integer addressId = 1;
        Integer customerId = 5;

        Address address = repo.findByIdAndCustomer(addressId, customerId);
        assertThat(address).isNotNull();
        System.out.println(address);
    }

    @Test
    public void testUpdate() {
        Integer addressId = 1;
        String phoneNumber = "+234-902-986-7835";
        Address address = repo.findById(addressId).get();
        address.setDefaultForShipping(false);
        address.setPhoneNumber(phoneNumber);

        Address updatedAddress = repo.save(address);
        assertThat(updatedAddress.getPhoneNumber()).isEqualTo(phoneNumber);
    }

    @Test
    public void testDeleteByIdAndCustomer() {
        Integer addressId = 1;
        Integer customerId = 5;

        repo.deleteByIdAndCustomer(addressId, customerId);
        Address address = repo.findByIdAndCustomer(addressId, customerId);
        assertThat(address).isNull();
    }

    @Test
    public void testSetDefault() {
        Integer addressId = 8;
        repo.setDefaultAddress(addressId);

        Address address = repo.findById(addressId).get();
        assertThat(address.isDefaultForShipping()).isTrue();
    }

    @Test
    public void testSetNonDefaultAddresses(){
        Integer addressId = 8;
        Integer customerId = 5;
        repo.setNonDefaultForOthers(addressId, customerId);
    }

    @Test
    public void testGetDefault(){
       Integer customerId = 5;
       Address address = repo.findDefaultByCustomer(customerId);
       assertThat(address).isNotNull();
        System.out.println(address);
    }
}
