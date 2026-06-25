package com.example.demo.shared.entity;

import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLRestriction;
 

@Getter
@MappedSuperclass
@SQLRestriction("deleted_at IS NULL") // ← filters soft-deleted records
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private String id; // ✅ String to avoid MySQL binary UUID issue

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}