package com.rsm.walletflow.service;

import com.rsm.walletflow.dto.CreateUserRequest;
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
}
