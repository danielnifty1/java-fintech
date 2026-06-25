package com.example.demo.wallet.repository;

import com.example.demo.shared.enums.Currency;
import com.example.demo.wallet.entity.WalletEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<WalletEntity, String> {

    List<WalletEntity> findAllByUserId(String userId);

    Optional<WalletEntity> findByUserIdAndCurrency(String userId, Currency currency);

    @Query("SELECT w FROM WalletEntity w WHERE w.userId = :userId " +
            "AND (:currency IS NULL OR w.currency = :currency)")
    Optional<WalletEntity> findByUserIdAndOptionalCurrency(
            @Param("userId") String userId,
            @Param("currency") Currency currency);

    Optional<WalletEntity> findByWalletId(String walletId);

    boolean existsByUserId(String userId);

    boolean existsByUserIdAndCurrency(String userId, Currency currency);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM WalletEntity w WHERE w.walletId = :walletId")
    Optional<WalletEntity> findByWalletIdForUpdate(@Param("walletId") String walletId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM WalletEntity w WHERE w.userId = :userId AND w.currency = :currency")
    Optional<WalletEntity> findByUserIdAndCurrencyForUpdate(
            @Param("userId") String userId,
            @Param("currency") Currency currency);
}