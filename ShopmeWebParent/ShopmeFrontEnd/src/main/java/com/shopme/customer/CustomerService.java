package com.shopme.customer;

import com.shopme.entity.Country;
import com.shopme.entity.Customer;
import com.shopme.settings.CountryRepository;
import jakarta.transaction.Transactional;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CustomerService {

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomerRepository customerRepository;

    public List<Country> listAllCountry() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    public boolean isEmailUnique(String email) {
        return customerRepository.findByEmail(email) == null;
    }

    public void registerCustomer(Customer customer) {
        encodePassword(customer);
        customer.setEnabled(false);
        customer.setCreatedTime(LocalDateTime.now());

        String ramdomCode = RandomString.make(64);
        customer.setVerficationCode(ramdomCode);
        System.out.println("verfication Code" + ramdomCode);

        customerRepository.save(customer);

    }

    private void encodePassword(Customer customer) {
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
    }

    public boolean verify(String verficationCode) {
        Customer customer = customerRepository.findByVerficationCode(verficationCode);
        if (customer == null || customer.getEnabled()) {
            return false;
        } else {
            customerRepository.enable(customer.getId());
            return true;
        }
    }
}
