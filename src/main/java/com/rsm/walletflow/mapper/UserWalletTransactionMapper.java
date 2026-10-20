package com.rsm.walletflow.mapper;

import com.rsm.walletflow.dto.UserTransferMoneyResponse;
import com.rsm.walletflow.entity.WalletTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper( componentModel = "spring")
public interface UserWalletTransactionMapper {

    @Mapping(target = "senderName", source= "walletTransaction.senderWallet.user.name")
    @Mapping(target = "receiverName", source= "walletTransaction.receiverWallet.user.name")
    @Mapping(target= "transferAmount", source="amount")
    UserTransferMoneyResponse toUserTransferMoneyResDto(WalletTransaction walletTransaction);
}
