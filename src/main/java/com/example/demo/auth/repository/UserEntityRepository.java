package com.example.demo.auth.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
 

import com.example.demo.auth.entity.UserEntity;


public interface UserEntityRepository extends JpaRepository<UserEntity, String> {
   
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findById(String userId);

    boolean existsByEmail(String email);
    boolean existsById(String Id);




}
 