package com.rsm.walletflow.service;

import com.rsm.walletflow.dto.UserBalance;
import com.rsm.walletflow.dto.UserWallet;
import com.rsm.walletflow.entity.User;
import com.rsm.walletflow.entity.Wallet;
import com.rsm.walletflow.mapper.UserWalletMapper;
import com.rsm.walletflow.repository.UserRepository;
import com.rsm.walletflow.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserWalletMapper userWalletMapper;
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
        return userWalletMapper.toUserBalanceDto(walletRepository.getWalletById(walletId));
    }
}
