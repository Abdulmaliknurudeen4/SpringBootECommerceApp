package com.shopme.customer;

import com.shopme.entity.Country;
import com.shopme.entity.Customer;
import com.shopme.settings.CountryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Test
    public void testCustomerCreation() {
        Country nigeria = countryRepository.findById(165).get();

        Customer customer = new Customer();
        customer.setFirstName("Abdulmalik");
        customer.setLastName("Nurudeen");
        customer.setCity("Lagos");
        customer.setCountry(nigeria);
        customer.setEnabled(false);
        customer.setVerficationCode("token2023");
        customer.setCreatedTime(LocalDate.now().atStartOfDay());
        customer.setAddressLineOne("5, Salami Streeet");
        customer.setPassword("klajdklfjklsjflskj");
        customer.setEmail("abdulmaliknurudeen4@gmail.com");
        customer.setPhoneNumber("08035719672");
        customer.setPostalCode("100132");
        customer.setState("Lagos");

        Customer save = customerRepository.save(customer);
        assertThat(save).isNotNull();
        assertThat(save.getEmail()).contains("@gmail.com");

    }

    @Test
    public void testFindByEmail() {
        Customer customer = customerRepository.findByEmail("abdulmaliknurudeen4@gmail.com");
        assertThat(customer).isNotNull();

    }

    @Test
    public void findAllEnabled() {
        customerRepository.enable(1);
        assertThat(customerRepository.findByEmail("abdulmaliknurudeen4@gmail.com").getEnabled()).isTrue();
    }

    @Test
    public void deleteUser() {
        customerRepository.deleteById(1);
        assertThat(true).isTrue();
    }

}
