package com.shopme.customer;

import com.shopme.entity.Country;
import com.shopme.entity.Customer;
import com.shopme.settings.SettingService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private SettingService settingService;


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
        customerService.sendVerificationEmail(request, customer);
        model.addAttribute("pageTitle", "Registration Successful.!");
        return "register/register_sucess";
    }


    @GetMapping("/verify")
    public String verifyAccount(@Param("code") String code, Model model) {
        boolean verified = customerService.verify(code);
        return "register/" + (verified ? "verify_success" : "verify_error");
    }
}
