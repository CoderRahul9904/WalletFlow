package com.rsm.walletflow.controller;

import com.rsm.walletflow.dto.UserTransferMoneyRequest;
import com.rsm.walletflow.dto.UserTransferMoneyResponse;
import com.rsm.walletflow.service.WalletTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class WalletTransactionController {

    private final WalletTransactionService walletTransactionService;

    @PostMapping("/transfer/{id}")
    public ResponseEntity<UserTransferMoneyResponse> transferMoney(@PathVariable Long id, @RequestBody UserTransferMoneyRequest request){
        return ResponseEntity.ok(walletTransactionService.transferMoney(id, request));
    }
}
