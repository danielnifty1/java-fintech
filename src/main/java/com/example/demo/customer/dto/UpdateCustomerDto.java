package com.example.demo.customer.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateCustomerDto {

    private String firstName;
    private String middleName;
    private String lastName;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String gender;
    private String maritalStatus;
    private String nationality;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String postalCode;

    // updatable images
    private String profileImage;
    private String signature;
    private String proofOfAddress;

    // ❌ bvn, nin, idType, idNumber, idFront, idBack not updatable
    // — those are KYC documents, should go through a separate KYC resubmit flow
}