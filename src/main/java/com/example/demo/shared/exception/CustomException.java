package com.example.demo.shared.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {

    private final HttpStatus status;

    // with status
    public CustomException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    // default to 400 if no status provided
    public CustomException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public HttpStatus getStatus() {  // ✅ this is what was missing
        return status;
    }
}