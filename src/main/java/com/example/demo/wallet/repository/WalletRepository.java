package com.example.demo.wallet.repository;

import com.example.demo.wallet.entity.WalletEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<WalletEntity, String> {

    Optional<WalletEntity> findByUserId(String userId);

    Optional<WalletEntity> findByWalletId(String walletId);

    boolean existsByUserId(String userId);

    // ← pessimistic write lock — blocks concurrent access
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM WalletEntity w WHERE w.walletId = :walletId")
    Optional<WalletEntity> findByWalletIdForUpdate(@Param("walletId") String walletId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM WalletEntity w WHERE w.userId = :userId")
    Optional<WalletEntity> findByUserIdForUpdate(@Param("userId") String userId);
}