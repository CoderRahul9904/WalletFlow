package com.rsm.walletflow.service;

import com.rsm.walletflow.dto.CreateUserRequest;
import com.rsm.walletflow.dto.RequestUser;
import com.rsm.walletflow.dto.SignUpResponse;
import com.rsm.walletflow.entity.User;
import com.rsm.walletflow.exception.ResourceNotFoundException;
import com.rsm.walletflow.mapper.UserMapper;
import com.rsm.walletflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public SignUpResponse createUser(CreateUserRequest request){
        User entity = userMapper.toEntity(request);
        entity.setCreatedAt(LocalDateTime.now());
        return userMapper.toDto(userRepository.save(entity));
    }

    public RequestUser getUserById(Long id){
        return userMapper.toRequestUserDto(userRepository.getUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id)));
    }

    public RequestUser getUserByEmail(String email){
        return userMapper.toRequestUserDto(userRepository.getUserByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email)));
    }
}
