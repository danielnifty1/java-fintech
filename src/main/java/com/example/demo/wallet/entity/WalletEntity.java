package com.example.demo.wallet.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

 
import org.hibernate.annotations.SQLRestriction;
 

import com.example.demo.shared.entity.BaseEntity;
import com.example.demo.shared.enums.Currency;

 

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@SQLRestriction("deleted_at IS NULL") // ← filters soft-deleted records
@Table(name = "wallets")
public class WalletEntity  extends BaseEntity{
    @Column(nullable = false)
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



 

    public enum Status {
        ACTIVE, FROZEN, CLOSED
    }
}