package com.example.bankapp.transfer.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmailNotificationMapper {

    @Mapping(target = "id", ignore = true)
    EmailNotification toEntity(EmailNotificationRequestDto dto);

    EmailNotificationRequestDto toDto(EmailNotification entity);
}
