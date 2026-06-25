package com.example.demo.wallet.controller;

import com.example.demo.auth.entity.UserEntity;
import com.example.demo.shared.annotation.CurrentUser;
import com.example.demo.shared.enums.Currency;
import com.example.demo.shared.responses.ApiResponse;
import com.example.demo.transactions.entity.TransactionEntity;
import com.example.demo.wallet.dto.CreditDebitDto;
import com.example.demo.wallet.dto.WalletResponseDto;
 
import com.example.demo.wallet.entity.WalletEntity;
import com.example.demo.wallet.service.WalletService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@AllArgsConstructor
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;
    private static final Logger logger =
    LoggerFactory.getLogger(WalletService.class);

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<WalletEntity>> createWallet(
            @CurrentUser UserEntity user,
            @RequestParam(defaultValue = "NGN") Currency currency) {
        WalletEntity wallet = walletService.createWallet(user.getId(), currency);
        return ResponseEntity.ok(ApiResponse.success("Wallet created", wallet));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WalletResponseDto>>> getWallet(
            @CurrentUser UserEntity user) {
        List<WalletResponseDto> wallet = walletService.getWalletsByUserId(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Wallet fetched", wallet));
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<ApiResponse<WalletResponseDto>> getWalletById(
            @CurrentUser UserEntity user,
            @PathVariable String walletId
            
        ) {
        WalletResponseDto wallet = walletService.getWalletByWalletId(walletId);
        return ResponseEntity.ok(ApiResponse.success("Wallet fetched", wallet));
    }


    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getBalance(
            @CurrentUser UserEntity user) {
        Map<String, BigDecimal> balances = walletService.getAllWalletBalances(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Balances fetched", balances));
    }

    // @PostMapping("/credit")
    // public ResponseEntity<ApiResponse<TransactionEntity>> credit(
    //         @Valid @RequestBody CreditDebitDto dto) {
    //     TransactionEntity txn = walletService.credit(dto);
    //     return ResponseEntity.ok(ApiResponse.success("Wallet credited", txn));
    // }

    // @PostMapping("/debit")
    // public ResponseEntity<ApiResponse<TransactionEntity>> debit(
    //         @Valid @RequestBody CreditDebitDto dto) {
    //     TransactionEntity txn = walletService.debit(dto);
    //     return ResponseEntity.ok(ApiResponse.success("Wallet debited", txn));
    // }

    // @GetMapping("/transactions")
    // public ResponseEntity<ApiResponse<List<TransactionEntity>>> getTransactions(
    //         @CurrentUser UserEntity user) {
    //     List<TransactionEntity> transactions = walletService.getTransactions(user.getId());
    //     return ResponseEntity.ok(ApiResponse.success("Transactions fetched", transactions));
    // }
}