package com.rsm.walletflow.service;

import com.rsm.walletflow.config.TransactionStatus;
import com.rsm.walletflow.config.TransactionType;
import com.rsm.walletflow.dto.UserBalance;
import com.rsm.walletflow.dto.UserDepositResponse;
import com.rsm.walletflow.dto.UserWallet;
import com.rsm.walletflow.dto.UserWithDrawResponse;
import com.rsm.walletflow.entity.User;
import com.rsm.walletflow.entity.Wallet;
import com.rsm.walletflow.entity.WalletTransaction;
import com.rsm.walletflow.mapper.UserWalletMapper;
import com.rsm.walletflow.repository.UserRepository;
import com.rsm.walletflow.repository.WalletRepository;
import com.rsm.walletflow.repository.WalletTransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserWalletMapper userWalletMapper;
    private final WalletTransactionRepository walletTransactionRepository;
    private final UserRepository userRepository;

    @Transactional
    public UserWallet createWallet(Long id){

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if(walletRepository.existsByUserId(id)){
            throw new RuntimeException(
                    "Wallet already exists for this user");
        }
        return userWalletMapper.toUserWalletDto(walletRepository.save(
                Wallet.builder().user(user).createdAt(LocalDateTime.now()).build()
        ));

    }

    public UserBalance getBalance(Long walletId) {
        return userWalletMapper.toUserBalanceDto(walletRepository.getWalletById(walletId).orElseThrow(() -> new RuntimeException("Wallet not found error 404")));
    }

    @Transactional
    public UserDepositResponse depositAmount(Long walletId, BigDecimal depositAmount){
        if (depositAmount == null ||
                depositAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Deposit amount must be greater than zero");
        }
        Wallet wallet=walletRepository.getWalletById(walletId).orElseThrow(() -> new RuntimeException("Wallet not found error 404"));
        wallet.setBalance(wallet.getBalance().add(depositAmount));
        WalletTransaction walletTransaction=WalletTransaction
                .builder()
                .amount(depositAmount)
                .receiverWallet(wallet)
                .status(TransactionStatus.SUCCESS)
                .type(TransactionType.DEPOSIT)
                .reference(UUID.randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .build();
        walletTransactionRepository.save(walletTransaction);
        return userWalletMapper.toUserDepositDto(wallet, depositAmount);
    }

    @Transactional
    public UserWithDrawResponse withDrawAmount(Long walletId, BigDecimal withDrawAmount){
        Wallet wallet=walletRepository.getWalletById(walletId).orElseThrow(() -> new RuntimeException("Wallet not found error 404"));
        if(withDrawAmount.compareTo(wallet.getBalance()) > 0){
            throw new IllegalArgumentException(
                    "Withdraw amount must be less than Available Balance");
        }
        wallet.setBalance(wallet.getBalance().subtract(withDrawAmount));
        WalletTransaction walletTransaction=WalletTransaction
                .builder()
                .amount(withDrawAmount)
                .senderWallet(wallet)
                .status(TransactionStatus.SUCCESS)
                .type(TransactionType.WITHDRAWAL)
                .reference(UUID.randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .build();
        walletTransactionRepository.save(walletTransaction);
        return userWalletMapper.toUserWithDrawDto(wallet, withDrawAmount);
    }
}
