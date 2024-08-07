package com.shopme.customer;

import com.shopme.Utility;
import com.shopme.entity.AuthenticationType;
import com.shopme.entity.Country;
import com.shopme.entity.Customer;
import com.shopme.settings.CountryRepository;
import com.shopme.settings.EmailSettingBag;
import com.shopme.settings.SettingService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
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

    @Autowired
    private SettingService settingService;

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
        customer.setAuthenticationType(AuthenticationType.DATABASE);

        String ramdomCode = RandomString.make(64);
        customer.setVerficationCode(ramdomCode);
        System.out.println("verfication Code" + ramdomCode);

        customerRepository.save(customer);

    }

    public Customer updateCustomer(Customer customer) {

        Customer existingCustomer = null;
        // Updating an existing Customer.
        existingCustomer = customerRepository.findById(customer.getId()).get();

        // A DATABASE KIND OF USER
        if (existingCustomer.getAuthenticationType() == AuthenticationType.DATABASE) {
            if (customer.getPassword() == null) {
                // if the password is null, set to previous password.
                customer.setPassword(existingCustomer.getPassword());
            } else {
                encodePassword(customer);
            }
        } else {
            customer.setPassword(existingCustomer.getPassword());
        }
        customer.setCreatedTime(existingCustomer.getCreatedTime());
        customer.setEnabled(existingCustomer.getEnabled());
        customer.setVerficationCode(existingCustomer.getVerficationCode());
        customer.setAuthenticationType(existingCustomer.getAuthenticationType());
        customer.setResetPasswordToken(existingCustomer.getResetPasswordToken());
        return customerRepository.save(customer);

    }

    @Async
    public void sendVerificationEmail(String siteURL, Customer customer) throws MessagingException, UnsupportedEncodingException {
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
        String verifyURL = siteURL + "/verify?code=" + customer.getVerficationCode();
        content = content.replace("[[url]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);

        System.out.println("to Address" + toAddress);
        System.out.println("Verify URL" + verifyURL);
    }

    @Async
    public void sendResetPasswordTokenEmail(String link, String email) throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSettingBag = settingService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettingBag);

        String toAddress = email;
        String subject = "Here's the link to reset your password";

        String content = "<p>Hello,</p>" +
                "<p> You have requested to reset your password</p>" +
                "Click the link below to change your password" +
                "<p><a href = \"" + link + "\"> Change my Password </a></p>" +
                "<br><p>Ignore this email if you do remeber your password, or your have not made this request</p>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(emailSettingBag.getFromAddress(), emailSettingBag.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
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

    public void updateAuthenticationType(Customer customer, AuthenticationType authenticationType) {
        if (!customer.getAuthenticationType().equals(authenticationType)) {
            customerRepository.updateAuthenticationType(customer.getId(), authenticationType);
        }
    }

    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public void addNewCustomerUponOAuthLogin(String email, String name, AuthenticationType authenticationType, String countryCode) {
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setFirstName(name);
        customer.setEnabled(true);
        customer.setCreatedTime(LocalDateTime.now());
        customer.setAuthenticationType(authenticationType);
        customer.setPassword("");
        customer.setAddressLine1("");
        customer.setAddressLine2("");
        customer.setCity("");
        customer.setState("");
        customer.setPhoneNumber("");
        customer.setPostalCode("");
        customer.setCountry(countryRepository.findByCode(countryCode));

        setName(name, customer);
        customerRepository.save(customer);
    }

    private void setName(String name, Customer customer) {
        String[] nameArray = name.split(" ");
        if (nameArray.length < 2) {
            customer.setFirstName(name);
            customer.setLastName("");
        } else {
            String firstName = nameArray[0];
            customer.setFirstName(firstName);

            String lastName = name.replace(firstName + " ", "");
            customer.setLastName(lastName);

        }
    }

    public String udpateResetPasswordToken(String email) throws CustomerNotFoundExcpetion {
        // Reset Password should only work for users with DB registration only.
        Customer byEmail = customerRepository.findByEmail(email);
        if (byEmail != null) {
            String make = RandomString.make(30);
            byEmail.setResetPasswordToken(make);
            customerRepository.save(byEmail);
            return make;
        } else {
            throw new CustomerNotFoundExcpetion("Could not find any customer with the email " + email);
        }
    }

    public Customer getByResetPasswordToken(String token) {
        Customer byResetPasswordToken = customerRepository.findByResetPasswordToken(token);
        return byResetPasswordToken;
    }

    public void updatePassword(String token, String newPassword) throws CustomerNotFoundExcpetion {
        Customer customer = customerRepository.findByResetPasswordToken(token);
        if (customer == null) {
            throw new CustomerNotFoundExcpetion("Customer not found: invalid token");
        }
        customer.setPassword(newPassword);
        encodePassword(customer);
        customerRepository.save(customer);

    }
}
