package com.example.demo.auth.entity;

import com.example.demo.shared.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
 

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_email", columnNames = "email"),
        @UniqueConstraint(name = "uk_users_username", columnNames = "username")
    }
)
public class UserEntity extends BaseEntity{

    @Column(nullable = false, length = 100) // ✅ removed unique = true, handled above
    private String email;

    @Column(nullable = false)
    private String password;
}