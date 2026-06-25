package com.example.demo.wallet.dto;

import com.example.demo.wallet.entity.WalletEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WalletResponseDto {
    private String walletId;
    private String currency;
    private String status;
    private BigDecimal balance;
    private LocalDateTime createdAt;

    public static WalletResponseDto from(WalletEntity wallet, BigDecimal balance) {
        WalletResponseDto dto = new WalletResponseDto();
        dto.setWalletId(wallet.getWalletId());
        dto.setCurrency(wallet.getCurrency().name());
        dto.setStatus(wallet.getStatus().name());
        dto.setBalance(balance);
        dto.setCreatedAt(wallet.getCreatedAt());
        return dto;
    }
}