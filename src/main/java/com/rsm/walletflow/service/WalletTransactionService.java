package com.rsm.walletflow.service;

import com.rsm.walletflow.config.TransactionStatus;
import com.rsm.walletflow.config.TransactionType;
import com.rsm.walletflow.config.WalletMoneyFlowType;
import com.rsm.walletflow.dto.UserTransactionHistoryResponse;
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
        Wallet senderWallet=walletRepository.getWalletById(senderWalletId).orElseThrow(() -> new RuntimeException("Wallet not found error 404"));
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

    @Transactional
    public List<UserTransactionHistoryResponse> getUserTransactionHistory(
            Long ownerWalletId) {

        Wallet ownerWallet = walletRepository.findById(ownerWalletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

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
