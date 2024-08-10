package com.shopme.customer;

import com.shopme.Utility;
import com.shopme.entity.AuthenticationType;
import com.shopme.entity.Country;
import com.shopme.entity.Customer;
import com.shopme.security.ShopmeCustomerDetails;
import com.shopme.security.oauth.CustomerOAuthUser;
import com.shopme.settings.SettingService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private SettingService settingService;

    private static String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

    @GetMapping("/customer")
    public String showCustomerDetails(HttpServletRequest request,
                                      Model model) {
        String email = Utility.getEmailOfAuthenticationUser(request);
        Customer loggedInUser = customerService.getCustomerByEmail(email);
        List<Country> listCountries = customerService.listAllCountry();
        model.addAttribute("listCountries", listCountries);
        model.addAttribute("pageTitle", "Customer Details");
        model.addAttribute("customer", loggedInUser);
        model.addAttribute("authType", loggedInUser.getAuthenticationType() == AuthenticationType.DATABASE);

        return "customer/customer";
    }

    @PostMapping("/update_customer")
    public String updateCustomerDetails(Customer customer,
                                        Model model, HttpServletRequest request, RedirectAttributes ra) {
        customerService.updateCustomer(customer);
        ra.addAttribute("message", "Your account details have been updated.");
        udpateNameOfAuthenticationUser(request, customer);

        String redirectOption = request.getParameter("redirect");
        String redirectURL = "redirect:/customer";

        if ("address_book".equals(redirectOption)) {
            redirectURL = "redirect:/address_book";
        } else if ("cart".equals(redirectOption)) {
            redirectURL = "redirect:/cart";
        } else if ("checkout".equals(redirectOption)) {
            redirectURL = "redirect:/address_book?redirect=checkout";
        }

        return redirectURL;
    }

    private void udpateNameOfAuthenticationUser(HttpServletRequest request, Customer s) {
        Object principal = request.getUserPrincipal();
        String fullName = s.getFullName() + " " + s.getLastName();

        if (principal instanceof UsernamePasswordAuthenticationToken
            || principal instanceof RememberMeAuthenticationToken) {
            ShopmeCustomerDetails userDetails = getCustomerDetailsObject(principal);
            Customer authenticatedCustomer = userDetails.getCustomer();
            authenticatedCustomer.setFirstName(s.getFirstName());
            authenticatedCustomer.setLastName(s.getLastName());
        } else if (principal instanceof OAuth2AuthenticationToken oauth2Token) {
            CustomerOAuthUser oauthUser = (CustomerOAuthUser) oauth2Token.getPrincipal();
            oauthUser.setFullName(fullName);
        }

    }

    private ShopmeCustomerDetails getCustomerDetailsObject(Object principal) {
        ShopmeCustomerDetails customerDetails = null;
        if (principal instanceof UsernamePasswordAuthenticationToken token) {
            customerDetails = (ShopmeCustomerDetails) token.getPrincipal();
        } else if (principal instanceof RememberMeAuthenticationToken token) {
            customerDetails = (ShopmeCustomerDetails) token.getPrincipal();
        }

        return customerDetails;
    }

    @GetMapping("/register")
    public String showRegister(Model model) {
        List<Country> listCountryies = customerService.listAllCountry();
        model.addAttribute("listCountries", listCountryies);
        model.addAttribute("pageTitle", "Customer Registeration");
        model.addAttribute("customer", new Customer());

        return "register/register_form";
    }

    @PostMapping("/create_customer")
    public String createCustomer(Customer customer,
                                 Model model, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        customerService.registerCustomer(customer);
        customerService.sendVerificationEmail(getSiteURL(request), customer);
        model.addAttribute("pageTitle", "Registration Successful.!");
        return "register/register_sucess";
    }

    @GetMapping("/verify")
    public String verifyAccount(@Param("code") String code, Model model) {
        boolean verified = customerService.verify(code);
        return "register/" + (verified ? "verify_success" : "verify_error");
    }
}

