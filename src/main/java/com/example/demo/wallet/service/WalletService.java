package com.example.demo.wallet.service;

import com.example.demo.shared.exception.CustomException;
import com.example.demo.transactions.repository.TransactionRepository;
import com.example.demo.wallet.dto.CreditDebitDto;
import com.example.demo.wallet.dto.WalletResponseDto;

import com.example.demo.wallet.entity.WalletEntity;

import com.example.demo.wallet.repository.WalletRepository;

 
import lombok.AllArgsConstructor;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 

@Service
@AllArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    private static final Logger logger =
    LoggerFactory.getLogger(WalletService.class);

    @Transactional
    public WalletEntity createWallet(String userId, WalletEntity.Currency currency) {
        if (walletRepository.existsByUserId(userId)) {
            throw new CustomException("Wallet already exists for this user", HttpStatus.CONFLICT);
        }

        WalletEntity wallet = new WalletEntity();
        wallet.setUserId(userId);
        wallet.setWalletId(generateWalletId());
        wallet.setCurrency(currency);
        wallet.setStatus(WalletEntity.Status.ACTIVE);
        return walletRepository.save(wallet);
    }

    public WalletResponseDto getWalletByUserId(String userId) {
        logger.info("FETCHING FOR: {}",userId);
        WalletEntity wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException("Wallet not found", HttpStatus.NOT_FOUND));
        BigDecimal balance = getBalanceInternal(wallet.getId());
        return WalletResponseDto.from(wallet, balance);
    }

    public WalletResponseDto getWalletByWalletId(String walletId) {
        logger.info("FETCHING FOR: {}",walletId);
        WalletEntity wallet = walletRepository.findByWalletId(walletId)
                .orElseThrow(() -> new CustomException("Wallet not found", HttpStatus.NOT_FOUND));
        BigDecimal balance = getBalanceInternal(wallet.getId());
        return WalletResponseDto.from(wallet, balance);
    }

    // WalletService.java or TransactionService.java — calculation lives here
    public BigDecimal getBalanceInternal(String userId) {

        WalletEntity wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException("Wallet not found", HttpStatus.NOT_FOUND));
        String walletId = wallet.getWalletId();
        BigDecimal credits = transactionRepository.sumCredits(walletId);
        BigDecimal debits = transactionRepository.sumDebits(walletId);
        return credits.subtract(debits); // ← balance = credits - debits
    }

    // public List<TransactionEntity> getTransactions(String userId) {
    // WalletEntity wallet = walletRepository.findByUserId(userId)
    // .orElseThrow(() -> new CustomException("Wallet not found",
    // HttpStatus.NOT_FOUND));
    // return
    // transactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId());
    // }

    public BigDecimal getBalance(String userId) {
        WalletEntity wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException("Wallet not found", HttpStatus.NOT_FOUND));
        return getBalanceInternal(wallet.getId());
    }

    // ← used internally within locked transactions
    // private BigDecimal getBalanceInternal(String walletId) {
    // BigDecimal credits = transactionRepository.sumCredits(walletId);
    // BigDecimal debits = transactionRepository.sumDebits(walletId);
    // return credits.subtract(debits);
    // }

    // private TransactionEntity recordTransaction(String walletId,
    // CreditDebitDto dto,
    // TransactionEntity.Type type) {
    // TransactionEntity txn = new TransactionEntity();
    // txn.setWalletId(walletId);
    // txn.setType(type);
    // txn.setAmount(dto.getAmount());
    // txn.setCurrency(dto.getCurrency());
    // txn.setReference(generateReference());
    // txn.setExternalReference(dto.getExternalReference());
    // txn.setDescription(dto.getDescription());
    // txn.setStatus(TransactionEntity.Status.SUCCESS);
    // txn.setMetadata(dto.getMetadata());
    // return transactionRepository.save(txn);
    // }

    private String generateWalletId() {
        return "WLT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateReference() {
        return "TXN-" + Instant.now().toEpochMilli() + "-" +
                UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}