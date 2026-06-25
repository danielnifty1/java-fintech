package com.example.demo.transactions.dto;

import com.example.demo.shared.enums.Currency;
import com.example.demo.shared.enums.Gateway;
import com.example.demo.transactions.entity.TransactionEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class GatewayCallbackDto {

    private String externalReference; // gateway's reference
    private String walletId;
    private BigDecimal amount;
    private Currency currency = Currency.NGN;
    private Gateway gateway;
    private TransactionEntity.Status status;
    private String description;
    private Map<String, String> metadata;
}