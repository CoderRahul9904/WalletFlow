package com.rsm.walletflow.mapper;

import com.rsm.walletflow.dto.CreateUserRequest;
import com.rsm.walletflow.dto.RequestUser;
import com.rsm.walletflow.dto.SignUpResponse;
import com.rsm.walletflow.entity.User;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserMapper {

    // For Single User fetching - Registration
    User toEntity(CreateUserRequest request);

    SignUpResponse toDto(User user);

    // For get user by id
    RequestUser toRequestUserDto(User user);
}
