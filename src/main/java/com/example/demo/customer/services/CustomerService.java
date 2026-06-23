package com.example.demo.customer.services;

import com.example.demo.customer.dto.CustomerOnboardingDto;
import com.example.demo.customer.entity.CustomerEntity;
import com.example.demo.customer.repository.CustomerRepository;
import com.example.demo.shared.exception.CustomException;
import com.example.demo.shared.services.CloudinaryService;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CloudinaryService cloudinaryService; // ✅ replaces local file storage

    public CustomerEntity onboard(CustomerOnboardingDto dto) {

        // duplicate checks
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

        return customerRepository.save(customer);
    }

    // fetch customer by userId (use after JWT auth)
    public CustomerEntity getByUserId(String userId) {
        return customerRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException("Customer profile not found"));
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

    // ✅ null + empty check helper
    private boolean isValidFile(MultipartFile file) {
        return file != null && !file.isEmpty();
    }

    private boolean isValidBase64(String value) {
        return value != null && !value.isBlank();
    }
}