package com.example.demo.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtClaims {
    private String email;    // sub
    private String userId;   // userId claim
    private long issuedAt;   // iat
    private long expiration; // exp
}