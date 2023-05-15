package com.shopme.customer;

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

@Controller
public class ForgotPasswordController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private SettingService settingService;

    @GetMapping("/forgot_password")
    public String showRequestForm() {
        return "customer/forgot_password_form";
    }

    @PostMapping("/forgot_password")
    public String processRequestForm(HttpServletRequest request,
                                     Model model) {
        String email = request.getParameter("email");
        try {
            String token = customerService.udpateResetPasswordToken(email);
            String siteURL = getSiteURL(request) + "/reset_password?token=" + token;
            customerService.sendResetPasswordTokenEmail(siteURL, email);
            model.addAttribute("message", "We have sent an email to the email to reset Your password");
        } catch (CustomerNotFoundExcpetion e) {
            model.addAttribute("error", e.getMessage());
            e.printStackTrace();
        } catch (MessagingException | UnsupportedEncodingException e) {
            model.addAttribute("error", "Could not send email");
        }
        return "customer/forgot_password_form";
    }


    private static String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

    @GetMapping("/reset_password")
    public String showResetForm(@Param("token") String token, Model model) {
        Customer byResetPasswordToken = customerService.getByResetPasswordToken(token);
        System.out.println(token);
        if (byResetPasswordToken != null) {
            model.addAttribute("token", token);
        } else {
            model.addAttribute("message", "Invalid message");
            return "message";
        }
        return "customer/reset_password_form";
    }

    @PostMapping("/reset_password")
    public String processResetForm(HttpServletRequest request, Model model) {
        String token = request.getParameter("token");
        String password = request.getParameter("password");

        try {
            customerService.updatePassword(token, password);
            model.addAttribute("pageTitle", "Password Changed.");
            model.addAttribute("message", "Password Has Been Changed. Login In");
            return "message";

        } catch (CustomerNotFoundExcpetion e) {
            model.addAttribute("pageTitle", "Invalid Token");
            model.addAttribute("message", e.getMessage());
            return "message";
        }
    }

}
