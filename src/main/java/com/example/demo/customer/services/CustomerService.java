package com.example.demo.customer.services;

import com.example.demo.auth.entity.UserEntity;
import com.example.demo.auth.repository.UserEntityRepository;
import com.example.demo.customer.dto.CustomerOnboardingDto;
import com.example.demo.customer.dto.UpdateCustomerDto;
import com.example.demo.customer.entity.CustomerEntity;
import com.example.demo.customer.repository.CustomerRepository;
import com.example.demo.shared.enums.Currency;
import com.example.demo.shared.exception.CustomException;
import com.example.demo.shared.services.CloudinaryService;
import com.example.demo.wallet.service.WalletService;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CloudinaryService cloudinaryService; // ✅ replaces local file storage
    private final UserEntityRepository userEntityRepository;
    private final WalletService walletService;
     

    public CustomerEntity onboard(CustomerOnboardingDto dto, String userId) {

        UserEntity getCustomer = getUserById(userId);

        String email = getCustomer.getEmail();
        // duplicate checks

        if(customerRepository.existsByUserId(userId))
            throw new CustomException("User Already onboarded ");


        if (customerRepository.existsByBvn(dto.getBvn()))
            throw new CustomException("BVN already registered");

        if (customerRepository.existsByPhoneNumber(dto.getPhoneNumber()))
            throw new CustomException("Phone number already registered");

        if (customerRepository.existsByNin(dto.getNin()))
            throw new CustomException("NIN already registered");

        CustomerEntity customer = new CustomerEntity();
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setMiddleName(dto.getMiddleName());
        customer.setPhoneNumber(dto.getPhoneNumber());
        customer.setEmail(email);
        customer.setUserId(userId);
        customer.setDateOfBirth(dto.getDateOfBirth());
        customer.setGender(CustomerEntity.Gender.valueOf(dto.getGender().toUpperCase()));
        customer.setAddressLine1(dto.getAddressLine1());
        customer.setAddressLine2(dto.getAddressLine2());
        customer.setCity(dto.getCity());
        customer.setState(dto.getState());
        customer.setCountry(dto.getCountry());
        customer.setPostalCode(dto.getPostalCode());
        customer.setBvn(dto.getBvn());
        customer.setNin(dto.getNin());
        customer.setIdType(CustomerEntity.IdType.valueOf(dto.getIdType().toUpperCase()));
        customer.setIdNumber(dto.getIdNumber());
        customer.setIdExpiryDate(dto.getIdExpiryDate());
        customer.setKycStatus(CustomerEntity.KycStatus.PENDING);
        customer.setAccountStatus(CustomerEntity.AccountStatus.INACTIVE);

        // ✅ upload to Cloudinary
        if (isValidBase64(dto.getProfileImage()))
            customer.setProfileImageUrl(
                    cloudinaryService.uploadBase64(dto.getProfileImage(), "profile-images"));

        if (isValidBase64(dto.getIdFront()))
            customer.setIdFrontUrl(
                    cloudinaryService.uploadBase64(dto.getIdFront(), "id-documents"));

        if (isValidBase64(dto.getIdBack()))
            customer.setIdBackUrl(
                    cloudinaryService.uploadBase64(dto.getIdBack(), "id-documents"));

        if (isValidBase64(dto.getSignature()))
            customer.setSignatureUrl(
                    cloudinaryService.uploadBase64(dto.getSignature(), "signatures"));

        if (isValidBase64(dto.getProofOfAddress()))
            customer.setProofOfAddressUrl(
                    cloudinaryService.uploadBase64(dto.getProofOfAddress(), "proof-of-address"));

                    walletService.createWallet(userId,Currency.NGN);
        return customerRepository.save(customer);
    }

    // fetch customer by userId (use after JWT auth)
    public CustomerEntity getByUserId(String userId) {
        return customerRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException("Customer profile not found", HttpStatus.NOT_FOUND));
    }

    public UserEntity getUserById(String userId) {
        return userEntityRepository.findById(userId)
                .orElseThrow(() -> new CustomException("Customer profile not found", HttpStatus.NOT_FOUND));

    }

    // update profile image only
    public CustomerEntity updateProfileImage(String userId, MultipartFile file) {
        CustomerEntity customer = getByUserId(userId);

        if (!isValidFile(file))
            throw new CustomException("Invalid file");

        // delete old image from Cloudinary if exists
        if (customer.getProfileImageUrl() != null)
            cloudinaryService.delete(customer.getProfileImageUrl());

        customer.setProfileImageUrl(cloudinaryService.upload(file, "profile-images"));
        return customerRepository.save(customer);
    }

    
    public CustomerEntity update(String userId, UpdateCustomerDto dto) {

        // fetch existing customer
        CustomerEntity customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException("Customer profile not found", HttpStatus.NOT_FOUND));
    
        // ✅ only update fields that are not null in the DTO
        if (isNotBlank(dto.getFirstName()))
            customer.setFirstName(dto.getFirstName());
    
        if (isNotBlank(dto.getLastName()))
            customer.setLastName(dto.getLastName());
    
        if (isNotBlank(dto.getMiddleName()))
            customer.setMiddleName(dto.getMiddleName());
    
        if (isNotBlank(dto.getPhoneNumber())) {
            // check phone not taken by another customer
            if (customerRepository.existsByPhoneNumber(dto.getPhoneNumber())
                    && !dto.getPhoneNumber().equals(customer.getPhoneNumber())) {
                throw new CustomException("Phone number already in use", HttpStatus.CONFLICT);
            }
            customer.setPhoneNumber(dto.getPhoneNumber());
        }
    
        if (dto.getDateOfBirth() != null)
            customer.setDateOfBirth(dto.getDateOfBirth());
    
        if (isNotBlank(dto.getGender()))
            customer.setGender(CustomerEntity.Gender.valueOf(dto.getGender().toUpperCase()));
    
        if (isNotBlank(dto.getMaritalStatus()))
            customer.setMaritalStatus(CustomerEntity.MaritalStatus.valueOf(dto.getMaritalStatus().toUpperCase()));
    
        if (isNotBlank(dto.getNationality()))
            customer.setNationality(dto.getNationality());
    
        if (isNotBlank(dto.getAddressLine1()))
            customer.setAddressLine1(dto.getAddressLine1());
    
        if (isNotBlank(dto.getAddressLine2()))
            customer.setAddressLine2(dto.getAddressLine2());
    
        if (isNotBlank(dto.getCity()))
            customer.setCity(dto.getCity());
    
        if (isNotBlank(dto.getState()))
            customer.setState(dto.getState());
    
        if (isNotBlank(dto.getCountry()))
            customer.setCountry(dto.getCountry());
    
        if (isNotBlank(dto.getPostalCode()))
            customer.setPostalCode(dto.getPostalCode());
    
        // ✅ update images — delete old from Cloudinary first
        if (isValidBase64(dto.getProfileImage())) {
            if (customer.getProfileImageUrl() != null)
                cloudinaryService.delete(customer.getProfileImageUrl());
            customer.setProfileImageUrl(
                cloudinaryService.uploadBase64(dto.getProfileImage(), "profile-images"));
        }
    
        if (isValidBase64(dto.getSignature())) {
            if (customer.getSignatureUrl() != null)
                cloudinaryService.delete(customer.getSignatureUrl());
            customer.setSignatureUrl(
                cloudinaryService.uploadBase64(dto.getSignature(), "signatures"));
        }
    
        if (isValidBase64(dto.getProofOfAddress())) {
            if (customer.getProofOfAddressUrl() != null)
                cloudinaryService.delete(customer.getProofOfAddressUrl());
            customer.setProofOfAddressUrl(
                cloudinaryService.uploadBase64(dto.getProofOfAddress(), "proof-of-address"));
        }
    
        return customerRepository.save(customer);
    }
    
  

    // check blank field  helper
    private boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
    
    // ✅ null + empty check helper
    private boolean isValidFile(MultipartFile file) {
        return file != null && !file.isEmpty();
    }

    private boolean isValidBase64(String value) {
        return value != null && !value.isBlank();
    }
}