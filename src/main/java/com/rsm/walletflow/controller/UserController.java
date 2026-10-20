package com.rsm.walletflow.controller;

import com.rsm.walletflow.dto.CreateUserRequest;
import com.rsm.walletflow.dto.RequestUser;
import com.rsm.walletflow.dto.SignUpResponse;
import com.rsm.walletflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<SignUpResponse> createUser(
            @RequestBody CreateUserRequest request) {

        return ResponseEntity.ok(userService.createUser(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequestUser> getUserById(
            @PathVariable Long id) {

        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<RequestUser> getUserByEmail(
            @RequestParam String email) {

        return ResponseEntity.ok(userService.getUserByEmail(email));
    }


}
