package com.example.demo.customer.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;

@Data
public class CustomerOnboardingDto {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String middleName;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    private String gender;

    private String maritalStatus;
    private String nationality;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String bvn;
    private String nin;
    private String idType;
    private String idNumber;
    private LocalDate idExpiryDate;

    // image files
   // ✅ base64 strings instead of MultipartFile
   private String profileImage;
   private String idFront;
   private String idBack;
   private String signature;
   private String proofOfAddress;
}