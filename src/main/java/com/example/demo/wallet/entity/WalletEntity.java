package com.example.demo.wallet.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.demo.shared.entity.BaseEntity;

import java.time.LocalDateTime; 

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "wallets")
public class WalletEntity  extends BaseEntity{
    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false, unique = true)
    private String walletId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @Version
    private Long version; // ← optimistic lock layer

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum Currency {
        NGN, USD
    }

    public enum Status {
        ACTIVE, FROZEN, CLOSED
    }
}