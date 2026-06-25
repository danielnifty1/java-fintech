package com.example.demo.customer.controller;


import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.auth.entity.UserEntity;
import com.example.demo.customer.dto.CustomerOnboardingDto;
import com.example.demo.customer.dto.UpdateCustomerDto;
import com.example.demo.customer.entity.CustomerEntity;
import com.example.demo.customer.services.CustomerService;
import com.example.demo.shared.annotation.CurrentUser;
import com.example.demo.shared.responses.ApiResponse;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@AllArgsConstructor
@RequestMapping("/customers")
public class CustomerController {
    private static final Logger logger =
    LoggerFactory.getLogger(CustomerController.class);
    
    final CustomerService customerService;


    @PostMapping(value = "/onboard")
    public ResponseEntity<ApiResponse<CustomerEntity>>onboard(
        @Valid @RequestBody CustomerOnboardingDto dto,
        @CurrentUser UserEntity currentUser
    ){
        CustomerEntity customer =customerService.onboard(dto,currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Onboarding successful", customer));
    }


    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<CustomerEntity>>updateCustoner(
        @RequestBody UpdateCustomerDto dto,
        @CurrentUser UserEntity currentUser
    ){
        CustomerEntity customer =customerService.update(currentUser.getId(),dto);
        return ResponseEntity.ok(ApiResponse.success("Onboarding successful", customer));
    } 

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CustomerEntity>> getMyProfile(
            @CurrentUser UserEntity currentUser) { // ✅ same here
                logger.info("Onboarding user: {}", currentUser.getId());
        CustomerEntity customer = customerService.getByUserId(currentUser.getId());

        return ResponseEntity.ok(ApiResponse.success("Profile fetched", customer));
    }

}
