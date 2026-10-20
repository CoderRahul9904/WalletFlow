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

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/users/{id}")
    public ResponseEntity<UserWallet> createWallet(
            @PathVariable Long id) {
        return ResponseEntity.ok(walletService.createWallet(id));
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<UserBalance> getBalance(
            @PathVariable Long id) {
        return ResponseEntity.ok(walletService.getBalance(id));
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<UserDepositResponse> deposit(
            @PathVariable Long id,
            @RequestBody BigDecimal depositAmount) {
        return ResponseEntity.ok(
                walletService.depositAmount(id, depositAmount));
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<UserWithDrawResponse> withdraw(
            @PathVariable Long id,
            @RequestBody BigDecimal withdrawAmount) {
        return ResponseEntity.ok(
                walletService.withDrawAmount(id, withdrawAmount));
    }
}
