package com.example.bankapp.accounts.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User toEntity(RegisterUserRequestDto dto);
    UserResponseDto toDto(User user);
} 