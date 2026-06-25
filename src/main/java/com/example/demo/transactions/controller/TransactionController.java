package com.example.demo.transactions.controller;

import com.example.demo.auth.entity.UserEntity;
import com.example.demo.shared.annotation.CurrentUser;
import com.example.demo.shared.responses.ApiResponse;
import com.example.demo.transactions.dto.GatewayCallbackDto;
import com.example.demo.transactions.dto.InitiateTransactionDto;
import com.example.demo.transactions.dto.TransactionResponseDto;
import com.example.demo.transactions.service.TransactionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/initiate")
    public ResponseEntity<ApiResponse<TransactionResponseDto>> initiate(
            @Valid @RequestBody InitiateTransactionDto dto,
            @CurrentUser UserEntity user) {
        TransactionResponseDto txn = TransactionResponseDto
                .from(transactionService.initiate(dto, user.getId()));
        return ResponseEntity.ok(ApiResponse.success("Transaction initiated", txn));
    }

    // ← webhook endpoint — called by Flutterwave, Paystack, Stripe
    @PostMapping("/webhook/{gateway}")
    public ResponseEntity<ApiResponse<TransactionResponseDto>> webhook(
            @PathVariable String gateway,
            @RequestBody GatewayCallbackDto dto) {
        // gateway path param can be used for signature verification per provider
        TransactionResponseDto txn = transactionService.handleGatewayCallback(dto);
        return ResponseEntity.ok(ApiResponse.success("Webhook processed", txn));
    }

    @PostMapping("/reverse/{reference}")
    public ResponseEntity<ApiResponse<TransactionResponseDto>> reverse(
            @PathVariable String reference) {
        TransactionResponseDto txn = transactionService.reverseTransaction(reference);
        return ResponseEntity.ok(ApiResponse.success("Transaction reversed", txn));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TransactionResponseDto>>> getMyTransactions(
            @CurrentUser UserEntity user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionResponseDto> transactions =
                transactionService.getUserTransactions(user.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success("Transactions fetched", transactions));
    }

    @GetMapping("/wallet/{walletId}")
    public ResponseEntity<ApiResponse<Page<TransactionResponseDto>>> getWalletTransactions(
            @PathVariable String walletId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionResponseDto> transactions =
                transactionService.getWalletTransactions(walletId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Transactions fetched", transactions));
    }

    @GetMapping("/{reference}")
    public ResponseEntity<ApiResponse<TransactionResponseDto>> getByReference(
            @PathVariable String reference) {
        TransactionResponseDto txn = transactionService.getByReference(reference);
        return ResponseEntity.ok(ApiResponse.success("Transaction fetched", txn));
    }
}