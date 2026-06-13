package com.lifeos.dto.response;

import java.time.LocalDateTime;

public class ErrorResponse {

    private String message;

    private LocalDateTime timestamp;

    public ErrorResponse() {
    }

    public ErrorResponse(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}