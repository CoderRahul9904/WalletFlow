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
import com.rsm.walletflow.exception.BusinessConflictException;
import com.rsm.walletflow.exception.InsufficientBalanceException;
import com.rsm.walletflow.exception.InvalidAmountException;
import com.rsm.walletflow.exception.ResourceNotFoundException;
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
                        new ResourceNotFoundException("User not found with id: " + id));

        if(walletRepository.existsByUserId(id)){
            throw new BusinessConflictException(
                    "Wallet already exists for this user");
        }
        return userWalletMapper.toUserWalletDto(walletRepository.save(
                Wallet.builder().user(user).createdAt(LocalDateTime.now()).build()
        ));

    }

    public UserBalance getBalance(Long walletId) {
        return userWalletMapper.toUserBalanceDto(walletRepository.getWalletById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + walletId)));
    }

    @Transactional
    public UserDepositResponse depositAmount(Long walletId, BigDecimal depositAmount){
        if (depositAmount == null ||
                depositAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(
                    "Deposit amount must be greater than zero");
        }
        Wallet wallet=walletRepository.getWalletById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + walletId));
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
        if (withDrawAmount == null ||
                withDrawAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(
                    "Withdraw amount must be greater than zero");
        }
        Wallet wallet=walletRepository.getWalletById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + walletId));
        if(withDrawAmount.compareTo(wallet.getBalance()) > 0){
            throw new InsufficientBalanceException(
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
