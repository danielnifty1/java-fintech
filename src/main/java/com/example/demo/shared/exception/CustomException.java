package com.example.demo.shared.exception;


public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}