package com.rsm.walletflow.controller;

import com.rsm.walletflow.dto.CreateUserRequest;
import com.rsm.walletflow.dto.RequestUser;
import com.rsm.walletflow.dto.SignUpResponse;
import com.rsm.walletflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/users/:id")
    public ResponseEntity<RequestUser> getUserById(@RequestParam("id") String id){
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/users/:email")
    public ResponseEntity<RequestUser> getUserByEmail(@RequestParam("email") String email){
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }
}
