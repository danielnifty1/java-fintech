package com.example.demo.wallet.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
 
import java.util.Map;

import com.example.demo.shared.enums.Currency;

@Data
public class CreditDebitDto {

    @NotBlank(message = "Wallet ID is required")
    private String walletId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    private Currency currency;

    private String description;
    private String externalReference;
    private Map<String, String> metadata;
}