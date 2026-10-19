package com.rsm.walletflow.controller;

import com.rsm.walletflow.dto.UserBalance;
import com.rsm.walletflow.dto.UserDepositResponse;
import com.rsm.walletflow.dto.UserWallet;
import com.rsm.walletflow.dto.UserWithDrawResponse;
import com.rsm.walletflow.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

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

    @PostMapping("/wallet/user/deposit/:id")
    public ResponseEntity<UserDepositResponse> getDepositAmount(@RequestParam("id") Long id, @RequestBody BigDecimal depositAmount){
        return ResponseEntity.ok(walletService.depositAmount(id, depositAmount));
    }

    @PostMapping("/wallet/user/withdraw/:id")
    public ResponseEntity<UserWithDrawResponse> getWithDrawAmount(@RequestParam("id") Long id, @RequestBody BigDecimal withDrawAmount){
        return ResponseEntity.ok(walletService.withDrawAmount(id, withDrawAmount));
    }
}
