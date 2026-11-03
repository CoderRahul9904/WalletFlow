package com.rsm.walletflow.mapper;

import com.rsm.walletflow.dto.CreateUserRequest;
import com.rsm.walletflow.dto.RequestUser;
import com.rsm.walletflow.dto.SignUpResponse;
import com.rsm.walletflow.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface UserMapper {

    // For Single User fetching - Registration
    // NOTE: password is stored directly into passwordHash for demo purposes.
    // In production this must be replaced with a BCrypt / Argon2 encoder.
    @Mapping(target = "passwordHash", source = "password")
    User toEntity(CreateUserRequest request);

    SignUpResponse toDto(User user);

    // For get user by id
    RequestUser toRequestUserDto(User user);
}
