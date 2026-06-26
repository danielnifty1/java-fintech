package com.example.demo.wallet.service;

import com.example.demo.shared.enums.Currency;
import com.example.demo.shared.exception.CustomException;
import com.example.demo.transactions.repository.TransactionRepository;
import com.example.demo.wallet.dto.WalletResponseDto;
import com.example.demo.wallet.entity.WalletEntity;
import com.example.demo.wallet.repository.WalletRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    private static final Logger logger = LoggerFactory.getLogger(WalletService.class);

    @Transactional
    public WalletEntity createWallet(String userId, Currency currency) {
        if (walletRepository.existsByUserIdAndCurrency(userId, currency)) {
            throw new CustomException(currency + " wallet already exists for this user", HttpStatus.CONFLICT);
        }
        WalletEntity wallet = new WalletEntity();
        wallet.setUserId(userId);
        wallet.setWalletId(generateWalletId());
        wallet.setCurrency(currency);
        wallet.setStatus(WalletEntity.Status.ACTIVE);
        return walletRepository.save(wallet);
    }

    // ← get all wallets for a user with their balances
    public List<WalletResponseDto> getWalletsByUserId(String userId) {
        logger.info("FETCHING WALLETS FOR: {}", userId);
        List<WalletEntity> wallets = walletRepository.findAllByUserId(userId);
        if (wallets.isEmpty()) {
            throw new CustomException("No wallets found", HttpStatus.NOT_FOUND);
        }
        return wallets.stream()
                .map(wallet -> WalletResponseDto.from(
                        wallet,
                        getBalanceInternal(wallet.getWalletId()) // ← walletId not userId
                ))
                .toList();
    }

    // ← get single wallet by currency
    public WalletResponseDto getWalletByUserIdAndCurrency(String userId, Currency currency) {
        WalletEntity wallet = walletRepository.findByUserIdAndCurrency(userId, currency)
                .orElseThrow(() -> new CustomException(currency + " wallet not found", HttpStatus.NOT_FOUND));
        return WalletResponseDto.from(wallet, getBalanceInternal(wallet.getWalletId()));
    }

    // ← get wallet by walletId
    public WalletResponseDto getWalletByWalletId(String walletId) {
        logger.info("FETCHING WALLET: {}", walletId);
        WalletEntity wallet = walletRepository.findByWalletId(walletId)
                .orElseThrow(() -> new CustomException("Wallet not found", HttpStatus.NOT_FOUND));
        return WalletResponseDto.from(wallet, getBalanceInternal(wallet.getWalletId()));
    }

    // ← get all balances as a map { "NGN": 45000, "USD": 200 }
    public Map<String, BigDecimal> getAllWalletBalances(String userId) {
        List<WalletEntity> wallets = walletRepository.findAllByUserId(userId);
        if (wallets.isEmpty()) {
            throw new CustomException("No wallets found", HttpStatus.NOT_FOUND);
        }
        return wallets.stream()
                .collect(Collectors.toMap(
                        wallet -> wallet.getCurrency().name(),
                        wallet -> getBalanceInternal(wallet.getWalletId())
                ));
    }

    // ← single wallet balance — takes walletId not userId
    public BigDecimal getBalanceInternal(String walletId) {
        BigDecimal credits = transactionRepository.sumCredits(walletId);
        BigDecimal debits = transactionRepository.sumDebits(walletId);
        return credits.subtract(debits);
    }

    private String generateWalletId() {
        return "WLT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

 
}