package com.rsm.walletflow.repository;

import com.rsm.walletflow.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    List<WalletTransaction> findBySenderWallet_IdOrReceiverWallet_Id(Long senderWalletId, Long receiverWalletId);
}

