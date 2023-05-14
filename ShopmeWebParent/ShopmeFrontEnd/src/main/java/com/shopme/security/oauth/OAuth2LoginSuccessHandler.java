package com.shopme.security.oauth;

import com.shopme.customer.CustomerService;
import com.shopme.entity.AuthenticationType;
import com.shopme.entity.Customer;
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
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final CustomerService customerService;

    @Autowired
    public OAuth2LoginSuccessHandler(@Lazy CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        CustomerOAuthUser oauthUser = (CustomerOAuthUser) authentication.getPrincipal();
        String email = oauthUser.getEmail();
        String name = oauthUser.getName();
        String countryCode = request.getLocale().getCountry();

        Customer customerByEmail = customerService.getCustomerByEmail(email);
        if (customerByEmail == null) {
            customerService.addNewCustomerUponOAuthLogin(email, name, AuthenticationType.GOOGLE, countryCode);
        } else {
            customerService.updateAuthenticationType(customerByEmail, AuthenticationType.GOOGLE);
        }
        System.out.println("OAuth2LoginSuccessHandler.onAuthenticationSuccess: email = " + email + ", name = " + name);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
