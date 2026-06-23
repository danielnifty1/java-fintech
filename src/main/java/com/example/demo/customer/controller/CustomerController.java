package com.example.demo.customer.controller;


import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.customer.dto.CustomerOnboardingDto;
import com.example.demo.customer.entity.CustomerEntity;
import com.example.demo.customer.services.CustomerService;
import com.example.demo.shared.responses.ApiResponse;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/customers")
public class CustomerController {
    
    final CustomerService customerService;
    @PostMapping(value = "/onboard", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CustomerEntity>>onboard(
        @Valid @ModelAttribute CustomerOnboardingDto dto
    ){
        CustomerEntity customer =customerService.onboard(dto);
        return ResponseEntity.ok(ApiResponse.success("Onboarding successful", customer));

    }

}
