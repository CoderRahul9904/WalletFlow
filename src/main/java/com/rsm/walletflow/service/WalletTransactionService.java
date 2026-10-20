package com.rsm.walletflow.service;

import com.rsm.walletflow.config.TransactionStatus;
import com.rsm.walletflow.config.TransactionType;
import com.rsm.walletflow.dto.UserTransferMoneyRequest;
import com.rsm.walletflow.dto.UserTransferMoneyResponse;
import com.rsm.walletflow.entity.Wallet;
import com.rsm.walletflow.entity.WalletTransaction;
import com.rsm.walletflow.mapper.UserWalletTransactionMapper;
import com.rsm.walletflow.repository.WalletRepository;
import com.rsm.walletflow.repository.WalletTransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletTransactionService {

    private final WalletTransactionRepository walletTransactionRepository;
    private final UserWalletTransactionMapper userWalletTransactionMapper;
    private final WalletRepository walletRepository;

    @Transactional
    public UserTransferMoneyResponse transferMoney(Long senderId, UserTransferMoneyRequest request){
        Wallet senderWallet=walletRepository.getWalletById(senderId).orElseThrow(() -> new RuntimeException("Wallet not found error 404"));
        if(request.getAmount().compareTo(senderWallet.getBalance()) > 0){
            throw new IllegalArgumentException(
                    "Transfer amount must be less than Available Balance");
        }

        senderWallet.setBalance(senderWallet.getBalance().subtract(request.getAmount()));
        Wallet receiverWallet= walletRepository.getWalletById(request.getReceiverWalletId()).orElseThrow(() -> new RuntimeException("Wallet not found error 404"));
        receiverWallet.setBalance(receiverWallet.getBalance().add(request.getAmount()));
        WalletTransaction walletTransaction=WalletTransaction
                .builder()
                .senderWallet(senderWallet)
                .receiverWallet(receiverWallet)
                .amount(request.getAmount())
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.SUCCESS)
                .reference(UUID.randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .build();
        walletTransactionRepository.save(walletTransaction);
        return userWalletTransactionMapper.toUserTransferMoneyResDto(walletTransaction);
    }
}
