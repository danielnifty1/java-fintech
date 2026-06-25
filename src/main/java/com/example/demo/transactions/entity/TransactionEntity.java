package com.example.demo.transactions.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

 

import com.example.demo.shared.entity.BaseEntity;
import com.example.demo.shared.enums.Currency;
import com.example.demo.shared.enums.Gateway;

import java.math.BigDecimal;
 
import java.util.Map;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "transactions")
public class TransactionEntity extends BaseEntity {


    @Column(nullable = false)
    private String walletId;

    @Column(nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type; // CREDIT or DEBIT

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @Column(nullable = false, unique = true)
    private String reference; // internal reference e.g TXN-xxx

    private String externalReference; // gateway reference

    @Enumerated(EnumType.STRING)
    private Gateway gateway; // FLUTTERWAVE, PAYSTACK, STRIPE, INTERNAL

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    private String description;

    private String failureReason;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "transaction_metadata",
        joinColumns = @JoinColumn(name = "transaction_id")
    )
    @MapKeyColumn(name = "meta_key")
    @Column(name = "meta_value")
    private Map<String, String> metadata;



    public enum Type {
        CREDIT, DEBIT
    }

    public enum Status {
        PENDING, SUCCESS, FAILED, REVERSED
    }

}