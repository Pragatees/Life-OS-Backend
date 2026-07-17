package com.lifeos.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/gemini/key")
    public ResponseEntity<String> geminiStatus() {

        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            return ResponseEntity.ok("Gemini API Key Not Loaded");
        }

        return ResponseEntity.ok(geminiApiKey);
    }
}