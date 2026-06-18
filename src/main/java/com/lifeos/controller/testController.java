package com.lifeos.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Value("${MAIL_PASSWORD:NOT_FOUND}")
    private String mailPassword;

    @GetMapping("/env")
    public String testEnv() {
        return "MAIL_PASSWORD = " + mailPassword;
    }
}