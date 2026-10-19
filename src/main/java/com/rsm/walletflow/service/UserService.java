package com.rsm.walletflow.service;

import com.rsm.walletflow.dto.CreateUserRequest;
import com.rsm.walletflow.dto.RequestUser;
import com.rsm.walletflow.dto.SignUpResponse;
import com.rsm.walletflow.entity.User;
import com.rsm.walletflow.mapper.UserMapper;
import com.rsm.walletflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public SignUpResponse createUser(CreateUserRequest request){
        return userMapper.toDto(userRepository.save(userMapper.toEntity(request)));
    }

    public RequestUser getUserById(String id){
        return userMapper.toRequestUserDto(userRepository.getUserById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id)));
    }

    public RequestUser getUserByEmail(String email){
        return userMapper.toRequestUserDto(userRepository.getUserByEmail(email).orElseThrow(() -> new RuntimeException("User not found with email: " + email)));
    }
}
