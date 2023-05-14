package com.shopme.security;

import com.shopme.customer.CustomerService;
import com.shopme.entity.AuthenticationType;
import com.shopme.entity.Customer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class DatabaseLoginSucessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private CustomerService customerService;


    @Autowired
    public DatabaseLoginSucessHandler(@Lazy CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
       ShopmeCustomerDetails userDetails = (ShopmeCustomerDetails) authentication.getPrincipal();
       Customer customer = userDetails.getCustomer();

        customerService.updateAuthenticationType(customer, AuthenticationType.DATABASE);
        super.onAuthenticationSuccess(request, response, chain, authentication);
    }
}
