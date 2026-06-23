package com.example.demo.customer.entity;

import com.example.demo.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(
    name = "customers",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_customers_email", columnNames = "email"),
        @UniqueConstraint(name = "uk_customers_phone", columnNames = "phone_number"),
        @UniqueConstraint(name = "uk_customers_bvn", columnNames = "bvn"),
        @UniqueConstraint(name = "uk_customers_nin", columnNames = "nin")
    }
)
public class CustomerEntity extends BaseEntity {

    // ── PERSONAL INFO ──────────────────────────────────────────
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "middle_name", length = 50)
    private String middleName;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "date_of_birth", nullable = false)
    private java.time.LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 10)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status", length = 20)
    private MaritalStatus maritalStatus;

    @Column(name = "nationality", length = 60)
    private String nationality;

    // ── ADDRESS ────────────────────────────────────────────────
    @Column(name = "address_line1", length = 150)
    private String addressLine1;

    @Column(name = "address_line2", length = 150)
    private String addressLine2;

    @Column(name = "city", length = 60)
    private String city;

    @Column(name = "state", length = 60)
    private String state;

    @Column(name = "country", length = 60)
    private String country;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    // ── IDENTITY / KYC ─────────────────────────────────────────
    @Column(name = "bvn", length = 11)
    private String bvn;                     // Bank Verification Number

    @Column(name = "nin", length = 11)
    private String nin;                     // National ID Number

    @Enumerated(EnumType.STRING)
    @Column(name = "id_type", length = 30)
    private IdType idType;                  // PASSPORT, DRIVERS_LICENSE, etc.

    @Column(name = "id_number", length = 50)
    private String idNumber;

    @Column(name = "id_expiry_date")
    private java.time.LocalDate idExpiryDate;

    // ── IMAGES / DOCUMENTS ─────────────────────────────────────
    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;         // selfie / profile photo

    @Column(name = "id_front_url", length = 500)
    private String idFrontUrl;              // front of ID card

    @Column(name = "id_back_url", length = 500)
    private String idBackUrl;               // back of ID card

    @Column(name = "signature_url", length = 500)
    private String signatureUrl;            // signature image

    @Column(name = "proof_of_address_url", length = 500)
    private String proofOfAddressUrl;       // utility bill, bank statement etc.

    // ── ACCOUNT STATUS / KYC STATUS ────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_status", length = 20)
    private KycStatus kycStatus = KycStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", length = 20)
    private AccountStatus accountStatus = AccountStatus.INACTIVE;

    @Column(name = "kyc_verified_at")
    private java.time.LocalDateTime kycVerifiedAt;

    // ── RELATIONSHIP TO USER ───────────────────────────────────
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;                  // FK to UserEntity (no join, keep it loose)

    // ── ENUMS ──────────────────────────────────────────────────
    public enum Gender {
        MALE, FEMALE, OTHER
    }

    public enum MaritalStatus {
        SINGLE, MARRIED, DIVORCED, WIDOWED
    }

    public enum IdType {
        PASSPORT,
        NATIONAL_ID,
        DRIVERS_LICENSE,
        VOTERS_CARD
    }

    public enum KycStatus {
        PENDING,        // just registered
        SUBMITTED,      // documents uploaded, awaiting review
        VERIFIED,       // KYC passed
        REJECTED,       // KYC failed
        RESUBMIT        // asked to reupload docs
    }

    public enum AccountStatus {
        ACTIVE,
        INACTIVE,
        SUSPENDED,
        CLOSED
    }
}