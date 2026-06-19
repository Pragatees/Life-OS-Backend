package com.lifeos.controller;

import com.lifeos.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final EmailService emailService;

    public TestController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/brevo")
    public String testBrevo() {

        emailService.sendEmail(
                "pragateesh.g2022ai-ds@sece.ac.in",
                "Life OS Test",
                "Hello from Life OS using Brevo!");

        return "Email Sent Successfully";
    }
}