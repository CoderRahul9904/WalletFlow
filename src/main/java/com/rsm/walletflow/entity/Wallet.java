package com.rsm.walletflow.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Wallet {
    @Id
    private Long id;


}
