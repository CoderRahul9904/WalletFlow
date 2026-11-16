package com.rsm.walletflow.repository;

import org.springframework.stereotype.Repository;
import com.rsm.walletflow.entity.DataIngestion;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository 
public interface DataIngestionRepository extends JpaRepository<DataIngestion, UUID> {
    
}
