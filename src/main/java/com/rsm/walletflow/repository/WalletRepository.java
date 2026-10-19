package com.rsm.walletflow.repository;

import com.rsm.walletflow.dto.UserBalance;
import com.rsm.walletflow.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    boolean existsByUserId(Long userId);

    <T> Wallet getWalletById(Long id);
}
