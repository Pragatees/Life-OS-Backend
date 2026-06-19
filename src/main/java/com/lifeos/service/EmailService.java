package com.lifeos.service;

import java.util.Properties;

import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final Environment environment;

    public EmailService(Environment environment) {
        this.environment = environment;
    }

    public void sendEmail(String to, String subject, String body) {

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // Hardcoded SMTP configuration
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        // Read directly from Environment Variables
        mailSender.setUsername(environment.getProperty("MAIL_USERNAME"));
        mailSender.setPassword(environment.getProperty("MAIL_PASSWORD"));

        // SMTP Properties
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Create Email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(environment.getProperty("MAIL_USERNAME"));
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        // Send Email
        mailSender.send(message);
    }
}