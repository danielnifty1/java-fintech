package com.example.demo.transactions.service;

import com.example.demo.shared.exception.CustomException;
import com.example.demo.transactions.dto.GatewayCallbackDto;
import com.example.demo.transactions.dto.InitiateTransactionDto;
import com.example.demo.transactions.dto.TransactionResponseDto;
import com.example.demo.transactions.entity.TransactionEntity;
import com.example.demo.transactions.repository.TransactionRepository;
// import com.example.demo.wallet.entity.WalletEntity;
import com.example.demo.wallet.repository.WalletRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    // ← called internally by WalletService or directly for gateway-initiated txns
    @Transactional
    public TransactionEntity initiate(InitiateTransactionDto dto, String userId) {

        // idempotency — don't create duplicate if external ref already exists
        if (dto.getExternalReference() != null &&
                transactionRepository.existsByExternalReference(dto.getExternalReference())) {
            return transactionRepository
                    .findByExternalReference(dto.getExternalReference())
                    .orElseThrow(() -> new CustomException("Transaction not found", HttpStatus.NOT_FOUND));
        }

        TransactionEntity txn = new TransactionEntity();
        txn.setWalletId(dto.getWalletId());
        txn.setUserId(userId);
        txn.setType(dto.getType());
        txn.setAmount(dto.getAmount());
        txn.setCurrency(dto.getCurrency());
        txn.setReference(generateReference());
        txn.setExternalReference(dto.getExternalReference());
        txn.setGateway(dto.getGateway());
        txn.setStatus(TransactionEntity.Status.PENDING); // ← starts as PENDING
        txn.setDescription(dto.getDescription());
        txn.setMetadata(dto.getMetadata());

        return transactionRepository.save(txn);
    }

    // ← called by webhook handlers from any gateway
    @Transactional
    public TransactionResponseDto handleGatewayCallback(GatewayCallbackDto dto) {

        // find the pending transaction by external reference
        TransactionEntity txn = transactionRepository
                .findByExternalReference(dto.getExternalReference())
                .orElseGet(() -> {
                    // if no existing txn, create one from callback
                    TransactionEntity newTxn = new TransactionEntity();
                    newTxn.setWalletId(dto.getWalletId());
                    newTxn.setType(TransactionEntity.Type.CREDIT);
                    newTxn.setAmount(dto.getAmount());
                    newTxn.setCurrency(dto.getCurrency());
                    newTxn.setReference(generateReference());
                    newTxn.setExternalReference(dto.getExternalReference());
                    newTxn.setGateway(dto.getGateway());
                    newTxn.setDescription(dto.getDescription());
                    newTxn.setMetadata(dto.getMetadata());
                    return newTxn;
                });

        // lock wallet for update if transaction is successful
        if (dto.getStatus() == TransactionEntity.Status.SUCCESS) {
            walletRepository.findByWalletIdForUpdate(dto.getWalletId())
                    .orElseThrow(() -> new CustomException("Wallet not found", HttpStatus.NOT_FOUND));
        }

        txn.setStatus(dto.getStatus());
        transactionRepository.save(txn);

        return TransactionResponseDto.from(txn);
    }

    @Transactional
    public TransactionResponseDto reverseTransaction(String reference) {
        TransactionEntity original = transactionRepository.findByReference(reference)
                .orElseThrow(() -> new CustomException("Transaction not found", HttpStatus.NOT_FOUND));

        if (original.getStatus() != TransactionEntity.Status.SUCCESS) {
            throw new CustomException("Only successful transactions can be reversed", HttpStatus.BAD_REQUEST);
        }

        // lock wallet
        walletRepository.findByWalletIdForUpdate(original.getWalletId())
                .orElseThrow(() -> new CustomException("Wallet not found", HttpStatus.NOT_FOUND));

        // create reversal transaction — opposite type
        TransactionEntity reversal = new TransactionEntity();
        reversal.setWalletId(original.getWalletId());
        reversal.setUserId(original.getUserId());
        reversal.setType(original.getType() == TransactionEntity.Type.CREDIT
                ? TransactionEntity.Type.DEBIT
                : TransactionEntity.Type.CREDIT);
        reversal.setAmount(original.getAmount());
        reversal.setCurrency(original.getCurrency());
        reversal.setReference(generateReference());
        reversal.setExternalReference("REVERSAL-" + original.getReference());
        reversal.setGateway(original.getGateway());
        reversal.setStatus(TransactionEntity.Status.SUCCESS);
        reversal.setDescription("Reversal of " + original.getReference());
        transactionRepository.save(reversal);

        // mark original as reversed
        original.setStatus(TransactionEntity.Status.REVERSED);
        transactionRepository.save(original);

        return TransactionResponseDto.from(reversal);
    }

    public Page<TransactionResponseDto> getUserTransactions(String userId, Pageable pageable) {
        return transactionRepository
                .findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(TransactionResponseDto::from);
    }

    public Page<TransactionResponseDto> getWalletTransactions(String walletId, Pageable pageable) {
        return transactionRepository
                .findByWalletIdOrderByCreatedAtDesc(walletId, pageable)
                .map(TransactionResponseDto::from);
    }

    public TransactionResponseDto getByReference(String reference) {
        return transactionRepository.findByReference(reference)
                .map(TransactionResponseDto::from)
                .orElseThrow(() -> new CustomException("Transaction not found", HttpStatus.NOT_FOUND));
    }

    private String generateReference() {
        return "TXN-" + Instant.now().toEpochMilli() + "-" +
                UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}