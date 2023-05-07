package com.shopme.customer;

import com.shopme.entity.Country;
import com.shopme.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CustomerController {
    @Autowired
    private CustomerService customerService;


    @GetMapping("/register")
    public String showRegister(Model model) {
        List<Country> listCountryies = customerService.listAllCountry();
        model.addAttribute("listCountries", listCountryies);
        model.addAttribute("pageTitle", "Customer Registeration");
        model.addAttribute("customer", new Customer());

        return "register/register_form";
    }

    @PostMapping("/create_customer")
    public String createCustomer(Customer cusomter, RedirectAttributes redirectAttributes, Model model) {

        return "account_created.";
    }
}
