package com.example.bankapp.cash.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class EmailNotificationRequestDto {
    private String email;
    private String subject;
    private String message;
}
