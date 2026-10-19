package com.rsm.walletflow.controller;

import com.rsm.walletflow.dto.UserBalance;
import com.rsm.walletflow.dto.UserWallet;
import com.rsm.walletflow.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/create/wallet/:id")
    public ResponseEntity<UserWallet> createWallet(@RequestParam("id") Long id){
        return ResponseEntity.ok(walletService.createWallet(id));
    }

    @GetMapping("/wallet/balance/:id")
    public ResponseEntity<UserBalance> getBalance(@RequestParam("id") Long id){
        return ResponseEntity.ok(walletService.getBalance(id));
    }
}
