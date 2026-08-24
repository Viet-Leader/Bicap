package com.bicap.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bicap.entity.Retailer;

public interface RetailerRepository
        extends JpaRepository<Retailer, Long> {
                Optional<Retailer> findByAccount_AccountId(Long accountId);
}
