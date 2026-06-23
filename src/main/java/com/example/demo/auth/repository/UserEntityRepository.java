package com.example.demo.auth.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.auth.entity.UserEntity;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
   
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);



}
 