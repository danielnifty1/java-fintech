package com.example.demo.customer.repository;

import com.example.demo.customer.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;


public interface CustomerRepository extends JpaRepository<CustomerEntity, String> {

    Optional<CustomerEntity> findByEmail(String email);
    Optional<CustomerEntity> findByUserId(String userId);
    Optional<CustomerEntity> findByBvn(String bvn);
    Optional<CustomerEntity> findByNin(String nin);
    Optional<CustomerEntity> findByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByBvn(String bvn);
    boolean existsByNin(String nin);
    boolean existsByUserId(String userId);
}