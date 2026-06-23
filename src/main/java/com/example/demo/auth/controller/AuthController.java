package com.example.demo.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import com.example.demo.auth.dto.AuthResponseDto;
import com.example.demo.auth.dto.UserDto;
import com.example.demo.auth.entity.UserEntity;
import com.example.demo.auth.service.AuthService;
import com.example.demo.jwt.JwtService;
import com.example.demo.shared.responses.ApiResponse;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDto userDto) {
        userService.register(userDto);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(@RequestBody UserDto userDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDto.getEmail(), userDto.getPassword()));

      

        if (authentication.isAuthenticated()) {
            UserEntity user = userService.findByEmail(userDto.getEmail());
            String token = jwtService.generateToken(authentication.getName(), user.getId());
            AuthResponseDto authResponse = new AuthResponseDto(token,user.getEmail());

            return ResponseEntity.ok(ApiResponse.success("Login Successfull",   authResponse));
        }

        return ResponseEntity.status(401).body(ApiResponse.error("Invalid Credentials"));
    }
}