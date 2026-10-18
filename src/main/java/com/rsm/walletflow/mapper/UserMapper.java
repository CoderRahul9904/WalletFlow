package com.rsm.walletflow.mapper;

import com.rsm.walletflow.dto.CreateUserRequest;
import com.rsm.walletflow.dto.SignUpResponse;
import com.rsm.walletflow.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // For Single User fetching
    User toEntity(CreateUserRequest request);
    SignUpResponse toDto(User user);
}
