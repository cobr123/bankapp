package com.example.bankapp.cash.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmailNotificationMapper {

    @Mapping(target = "id", ignore = true)
    EmailNotification toEntity(EmailNotificationRequestDto dto);

    EmailNotificationRequestDto toDto(EmailNotification entity);
}
