package com.shopme.admin.customer;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.entity.Customer;
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
public class CustomerService {

    public static final Integer CUSTOMERS_PER_PAGE = 10;

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private PasswordEncoder encoder;

    public List<Customer> listAll() {
        return (List<Customer>) customerRepository.findAll(Sort.by("firstName").ascending());
    }

    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
       helper.listEntities(pageNum, CUSTOMERS_PER_PAGE, customerRepository);
    }

    public Customer updateCustomer(Customer customer) throws CustomerNotFoundExcpetion {

        Customer existingCustomer;
        if (customer.getId() != null) {
            // Updating an existing Customer.
            existingCustomer = customerRepository.findById(customer.getId()).get();
            if (customer.getPassword() == null) {
                // if the password is null, set to previous password.
                customer.setPassword(existingCustomer.getPassword());
            } else {
                encodeCustomerPassword(customer);
            }
        } else {
            throw new CustomerNotFoundExcpetion("The Customer Is not present in the Database. Please refresh");
        }
        customer.setCreatedTime(existingCustomer.getCreatedTime());
        customer.setEnabled(existingCustomer.getEnabled());
        customer.setVerficationCode(existingCustomer.getVerficationCode());
        return customerRepository.save(customer);

    }

    private void encodeCustomerPassword(Customer customer) {
        String encodedPassword = encoder.encode(customer.getPassword());
        customer.setPassword(encodedPassword);
    }

    public boolean isEmailUnique(Integer id, String email) {
        boolean isCreatingNew = (id == null);
        Customer customerByEmail = customerRepository.getCustomerByEmail(email);
        if (customerByEmail == null)
            return true;
        if (isCreatingNew) {
            return customerByEmail == null;
        } else {
            return customerByEmail.getId() == id;
        }
    }

    public Customer getCustomer(Integer id) throws CustomerNotFoundExcpetion {
        // TODO Auto-generated method stub
        try {
            return customerRepository.findById(id).orElseThrow();
        } catch (NoSuchElementException e) {
            throw new CustomerNotFoundExcpetion("could not find customer with id " + id);
        }
    }

    public void deleteCustomer(Integer id) throws CustomerNotFoundExcpetion {
        Long customerCount = customerRepository.countById(id);
        if (customerCount == null || customerCount == 0) {
            throw new CustomerNotFoundExcpetion("Customer not Present !!");
        }
        customerRepository.deleteById(id);
    }

    public void setEnableCustomer(boolean status, Integer id) throws CustomerNotFoundExcpetion {
        Long customerCount = customerRepository.countById(id);
        if (customerCount == null || customerCount == 0) {
            throw new CustomerNotFoundExcpetion("Customer is not Present !!");
        }
        customerRepository.enableCustomerStatus(id, status);
    }

    public Customer getCustomerByEmail(String userMail) {
        return customerRepository.getCustomerByEmail(userMail);
    }

}
