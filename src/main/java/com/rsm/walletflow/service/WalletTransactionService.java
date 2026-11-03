package com.rsm.walletflow.service;

import com.rsm.walletflow.config.TransactionStatus;
import com.rsm.walletflow.config.TransactionType;
import com.rsm.walletflow.config.WalletMoneyFlowType;
import com.rsm.walletflow.dto.UserTransactionHistoryResponse;
import com.rsm.walletflow.dto.UserTransferMoneyRequest;
import com.rsm.walletflow.dto.UserTransferMoneyResponse;
import com.rsm.walletflow.entity.Wallet;
import com.rsm.walletflow.entity.WalletTransaction;
import com.rsm.walletflow.exception.InsufficientBalanceException;
import com.rsm.walletflow.exception.InvalidAmountException;
import com.rsm.walletflow.exception.ResourceNotFoundException;
import com.rsm.walletflow.mapper.UserWalletTransactionMapper;
import com.rsm.walletflow.repository.WalletRepository;
import com.rsm.walletflow.repository.WalletTransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletTransactionService {

    private final WalletTransactionRepository walletTransactionRepository;
    private final UserWalletTransactionMapper userWalletTransactionMapper;
    private final WalletRepository walletRepository;

    @Transactional
    public UserTransferMoneyResponse transferMoney(Long senderWalletId, UserTransferMoneyRequest request){
        if (request == null || request.getAmount() == null
                || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(
                    "Transfer amount must be greater than zero");
        }
        if (request.getReceiverWalletId() == null) {
            throw new InvalidAmountException("Receiver wallet id is required");
        }
        if (senderWalletId.equals(request.getReceiverWalletId())) {
            throw new InvalidAmountException("Self-transfer is not allowed");
        }

        Wallet senderWallet = walletRepository.getWalletById(senderWalletId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sender wallet not found with id: " + senderWalletId));

        if(request.getAmount().compareTo(senderWallet.getBalance()) > 0){
            throw new InsufficientBalanceException(
                    "Transfer amount must be less than Available Balance");
        }

        // Validate receiver BEFORE mutating any balances so a bad receiver never
        // leaves the sender's balance temporarily reduced inside the transaction.
        Wallet receiverWallet = walletRepository.getWalletById(request.getReceiverWalletId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Receiver wallet not found with id: " + request.getReceiverWalletId()));

        senderWallet.setBalance(senderWallet.getBalance().subtract(request.getAmount()));
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

    @Transactional
    public List<UserTransactionHistoryResponse> getUserTransactionHistory(
            Long ownerWalletId) {

        Wallet ownerWallet = walletRepository.findById(ownerWalletId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Wallet not found with id: " + ownerWalletId));

        List<WalletTransaction> transactions =
                walletTransactionRepository
                        .findBySenderWallet_IdOrReceiverWallet_Id(
                                ownerWalletId, ownerWalletId);

        return transactions.stream()
                .map(transaction -> {
                    String ownerName = ownerWallet.getUser().getName();
                    String counterPartyName;
                    WalletMoneyFlowType flowType;

                    switch (transaction.getType()) {
                        case DEPOSIT -> {
                            counterPartyName = "Wallet Deposit";
                            flowType = WalletMoneyFlowType.INCOMING;
                        }
                        case WITHDRAWAL -> {
                            counterPartyName = "Cash Withdrawal";
                            flowType = WalletMoneyFlowType.OUTGOING;
                        }
                        case TRANSFER -> {
                            boolean isSender = transaction.getSenderWallet()
                                    .getId().equals(ownerWalletId);

                            if (isSender) {
                                counterPartyName = transaction.getReceiverWallet()
                                        .getUser().getName();
                                flowType = WalletMoneyFlowType.OUTGOING;
                            } else {
                                counterPartyName = transaction.getSenderWallet()
                                        .getUser().getName();
                                flowType = WalletMoneyFlowType.INCOMING;
                            }
                        }
                        default -> throw new IllegalStateException(
                                "Unsupported transaction type");
                    }

                    return userWalletTransactionMapper
                            .toUserTransactionHistoryResDto(
                                    transaction,
                                    ownerName,
                                    counterPartyName,
                                    flowType);
                })
                .toList();
    }


}
