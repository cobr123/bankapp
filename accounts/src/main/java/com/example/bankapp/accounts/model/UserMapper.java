package com.example.bankapp.accounts.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    User toEntity(RegisterUserRequestDto dto);

    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "accounts", ignore = true)
    UserResponseDto toDto(User user);
} 