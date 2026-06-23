package com.example.demo.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDto {
    @NotBlank(message = "password is required")
    private String password;

    @NotBlank(message = "Email is required")
    private String email;

    
}
