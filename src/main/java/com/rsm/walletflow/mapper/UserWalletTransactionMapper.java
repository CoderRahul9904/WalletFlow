package com.rsm.walletflow.mapper;

import com.rsm.walletflow.config.WalletMoneyFlowType;
import com.rsm.walletflow.dto.UserTransactionHistoryResponse;
import com.rsm.walletflow.dto.UserTransferMoneyResponse;
import com.rsm.walletflow.entity.WalletTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper( componentModel = "spring")
public interface UserWalletTransactionMapper {

    @Mapping(target = "senderName", source= "walletTransaction.senderWallet.user.name")
    @Mapping(target = "receiverName", source= "walletTransaction.receiverWallet.user.name")
    @Mapping(target= "transferAmount", source="amount")
    UserTransferMoneyResponse toUserTransferMoneyResDto(WalletTransaction walletTransaction);

    @Mapping(target = "ownerName", source = "ownerName")
    @Mapping(target = "counterPartyName", source = "counterPartyName")
    @Mapping(target = "walletMoneyFlowType", source="walletMoneyFlowType")
    @Mapping(target="transactionTime", source="walletTransaction.createdAt")
    @Mapping(target = "transactionType", source = "walletTransaction.type")
    @Mapping(target = "transactionStatus", source = "walletTransaction.status")
    UserTransactionHistoryResponse toUserTransactionHistoryResDto(WalletTransaction walletTransaction, String ownerName, String counterPartyName, WalletMoneyFlowType walletMoneyFlowType);
}
