package com.rsm.walletflow.controller;

import com.rsm.walletflow.dto.CreateUserRequest;
import com.rsm.walletflow.dto.SignUpResponse;
import com.rsm.walletflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<SignUpResponse> createUser(
            @RequestBody CreateUserRequest request
            ){
        return ResponseEntity.ok(userService.createUser(request));
    }
}
