package com.shopme;

import com.shopme.customer.CustomerService;
import com.shopme.entity.Customer;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ControllerHelper {
    @Autowired
    private CustomerService customerService;

    public Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = Utility.getEmailOfAuthenticationUser(request);
        return customerService.getCustomerByEmail(email);
    }
}
