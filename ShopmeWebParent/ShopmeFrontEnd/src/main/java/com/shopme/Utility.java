package com.shopme;

import com.shopme.settings.EmailSettingBag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.mail.javamail.JavaMailSenderImpl;

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
}
