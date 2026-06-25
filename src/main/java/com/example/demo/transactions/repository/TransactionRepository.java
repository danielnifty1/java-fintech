package com.example.demo.transactions.repository;

import com.example.demo.transactions.entity.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {

    Page<TransactionEntity> findByWalletIdOrderByCreatedAtDesc(String walletId, Pageable pageable);

    Page<TransactionEntity> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    Optional<TransactionEntity> findByReference(String reference);

    Optional<TransactionEntity> findByExternalReference(String externalReference);

    boolean existsByReference(String reference);

    boolean existsByExternalReference(String externalReference);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionEntity t " +
           "WHERE t.walletId = :walletId " +
           "AND t.type = com.example.demo.transactions.entity.TransactionEntity.Type.CREDIT " +
           "AND t.status = com.example.demo.transactions.entity.TransactionEntity.Status.SUCCESS")
    BigDecimal sumCredits(@Param("walletId") String walletId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionEntity t " +
           "WHERE t.walletId = :walletId " +
           "AND t.type = com.example.demo.transactions.entity.TransactionEntity.Type.DEBIT " +
           "AND t.status = com.example.demo.transactions.entity.TransactionEntity.Status.SUCCESS")
    BigDecimal sumDebits(@Param("walletId") String walletId);
    
}