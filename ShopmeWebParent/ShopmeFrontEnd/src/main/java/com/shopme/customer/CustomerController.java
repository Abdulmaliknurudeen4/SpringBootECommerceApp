package com.shopme.customer;

import com.shopme.Utility;
import com.shopme.entity.Country;
import com.shopme.entity.Customer;
import com.shopme.settings.EmailSettingBag;
import com.shopme.settings.SettingService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
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
        sendVerificationEmail(request, customer);
        model.addAttribute("pageTitle", "Registration Successful.!");
        return "register/register_sucess";
    }

    @Async
    public void sendVerificationEmail(HttpServletRequest request, Customer customer) throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSettingBag = settingService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettingBag);


        String toAddress = customer.getEmail();
        String subject = emailSettingBag.getCustomerVerifySubject();
        String content = emailSettingBag.getCustomerVerifyContent();

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        System.out.println(emailSettingBag.getFromAddress() + emailSettingBag.getSenderName());
        System.out.println(emailSettingBag.getPassword());

        helper.setFrom(emailSettingBag.getFromAddress(), emailSettingBag.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", customer.getFullName());
        String verifyURL = Utility.getSiteURL(request) + "/verify?code=" + customer.getVerficationCode();
        content = content.replace("[[url]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);

        System.out.println("to Address" + toAddress);
        System.out.println("Verify URL" + verifyURL);
    }

    @GetMapping("/verify")
    public String verifyAccount(@Param("code") String code, Model model) {
        boolean verified = customerService.verify(code);
        return "register/" + (verified ? "verify_success" : "verify_error");
    }
}