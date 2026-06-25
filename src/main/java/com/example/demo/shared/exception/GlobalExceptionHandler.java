package com.example.demo.shared.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.demo.shared.responses.ApiResponse;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ✅ handles all CustomException — status comes from the exception itself
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
        return ResponseEntity
                .status(e.getStatus())
                .body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFound(UsernameNotFoundException e) {
        return ResponseEntity
                .status(404)
                .body(ApiResponse.error("No account found with that email"));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException e) {
        return ResponseEntity
                .status(401)
                .body(ApiResponse.error("Invalid credentials"));
    }

    // ✅ handles @Valid validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        return ResponseEntity
                .status(400)
                .body(ApiResponse.error(message));
    }

    // ✅ catch-all for unexpected errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception e) {
        return ResponseEntity
                .status(500)
                .body(ApiResponse.error("Something went wrong"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
public ResponseEntity<ApiResponse<Void>> handleEnumMismatch(MethodArgumentTypeMismatchException e) {
    String paramName = e.getName();
    String invalidValue = String.valueOf(e.getValue());
    
    // if the param is an enum, list the valid values
    String message = invalidValue + " is not a valid value for " + paramName;
    
    if (e.getRequiredType() != null && e.getRequiredType().isEnum()) {
        String validValues = Arrays.stream(e.getRequiredType().getEnumConstants())
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        message = invalidValue + " is not a valid " + paramName + ". Accepted values: " + validValues;
    }

    return ResponseEntity
            .status(400)
            .body(ApiResponse.error(message));
}
}