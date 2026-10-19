package com.rsm.walletflow.mapper;

import com.rsm.walletflow.dto.UserBalance;
import com.rsm.walletflow.dto.UserDepositResponse;
import com.rsm.walletflow.dto.UserWallet;
import com.rsm.walletflow.dto.UserWithDrawResponse;
import com.rsm.walletflow.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface UserWalletMapper {

    @Mapping(target = "userName", source = "user.name")
    @Mapping(target= "email", source= "user.email")
    @Mapping(target="walletId", source= "id")
    UserWallet toUserWalletDto(Wallet wallet);

    UserBalance toUserBalanceDto(Wallet wallet);

    @Mapping(target = "depositAmount", source = "depositAmount")
    @Mapping(target = "name", source = "wallet.user.name")
    @Mapping(target= "totalAmount", source="wallet.balance")
    UserDepositResponse toUserDepositDto(Wallet wallet, BigDecimal depositAmount);

    @Mapping(target = "withDrawAmount", source = "withDrawAmount")
    @Mapping(target = "name", source = "wallet.user.name")
    @Mapping(target= "totalAmount", source="wallet.balance")
    UserWithDrawResponse toUserWithDrawDto(Wallet wallet, BigDecimal withDrawAmount);
}
