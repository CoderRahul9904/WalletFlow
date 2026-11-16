package com.rsm.walletflow.entity;

import java.util.UUID;



import com.rsm.walletflow.config.DataIngestionStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ingestion_responses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataIngestion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID ingestionId;
    private DataIngestionStatus status;
    private String message;

}
