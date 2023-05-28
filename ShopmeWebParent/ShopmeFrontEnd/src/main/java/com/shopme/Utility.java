package com.shopme;

import com.shopme.security.oauth.CustomerOAuthUser;
import com.shopme.settings.EmailSettingBag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.util.Properties;

public class Utility {
    public static String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

    public static JavaMailSenderImpl prepareMailSender(EmailSettingBag settingBag) {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost(settingBag.getHost());
        javaMailSender.setPort(Integer.parseInt(settingBag.getPort()));
        javaMailSender.setUsername(settingBag.getUsername());
        javaMailSender.setPassword(settingBag.getPassword());

        Properties mailProperties = new Properties();
        mailProperties.setProperty("mail.smtp.auth", settingBag.getSmtpAuth());
        mailProperties.setProperty("mail.smtp.starttls.enable", settingBag.getSmtpSecured());
        mailProperties.setProperty("mail.smtp.ssl.enable", settingBag.getSmtpSecured());
        //
        mailProperties.setProperty("mail.transport.protocol", "smtp");
        mailProperties.setProperty("mail.debug", "true");

        javaMailSender.setJavaMailProperties(mailProperties);

        return javaMailSender;
    }

    public static String getEmailOfAuthenticationUser(HttpServletRequest request) {
        Object principal = request.getUserPrincipal();
        if(principal==null) {
            return null;
        }
        String customerEmail = null;

        if (principal instanceof UsernamePasswordAuthenticationToken
                || principal instanceof RememberMeAuthenticationToken) {
            customerEmail = request.getUserPrincipal().getName();
        } else if (principal instanceof OAuth2AuthenticationToken oauth2Token) {
            CustomerOAuthUser oauthUser = (CustomerOAuthUser) oauth2Token.getPrincipal();
            customerEmail = oauthUser.getEmail();
        }

        return customerEmail;

    }
}
