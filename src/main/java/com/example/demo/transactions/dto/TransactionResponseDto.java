package com.example.demo.transactions.dto;

import com.example.demo.shared.enums.Currency;
import com.example.demo.shared.enums.Gateway;
import com.example.demo.transactions.entity.TransactionEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class TransactionResponseDto {

    private String id;
    private String walletId;
    private String reference;
    private String externalReference;
    private TransactionEntity.Type type;
    private Gateway gateway;
    private BigDecimal amount;
    private Currency currency;
    private TransactionEntity.Status status;
    private String description;
    private String failureReason;
    private Map<String, String> metadata;
    private LocalDateTime createdAt;

    public static TransactionResponseDto from(TransactionEntity txn) {
        TransactionResponseDto dto = new TransactionResponseDto();
        dto.setId(txn.getId());
        dto.setWalletId(txn.getWalletId());
        dto.setReference(txn.getReference());
        dto.setExternalReference(txn.getExternalReference());
        dto.setType(txn.getType());
        dto.setGateway(txn.getGateway());
        dto.setAmount(txn.getAmount());
        dto.setCurrency(txn.getCurrency());
        dto.setStatus(txn.getStatus());
        dto.setDescription(txn.getDescription());
        dto.setFailureReason(txn.getFailureReason());
        dto.setMetadata(txn.getMetadata());
        dto.setCreatedAt(txn.getCreatedAt());
        return dto;
    }
}