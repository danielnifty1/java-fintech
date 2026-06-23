package com.example.demo.auth.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
 

import com.example.demo.auth.entity.UserEntity;


public interface UserEntityRepository extends JpaRepository<UserEntity, String> {
   
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);



}
 