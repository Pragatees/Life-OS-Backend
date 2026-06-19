package com.lifeos.controller;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final Environment environment;

    public TestController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping("/env")
    public String env() {

        return """
                MAIL_USERNAME = %s
                MAIL_PASSWORD = %s
                DB_USERNAME = %s
                JWT_SECRET = %s
                """
                .formatted(
                        environment.getProperty("MAIL_USERNAME"),
                        environment.getProperty("MAIL_PASSWORD"),
                        environment.getProperty("SPRING_DATASOURCE_USERNAME"),
                        environment.getProperty("JWT_SECRET")
                );
    }
}