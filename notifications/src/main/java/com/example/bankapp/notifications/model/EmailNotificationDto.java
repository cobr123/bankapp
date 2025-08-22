package com.example.bankapp.notifications.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@Builder
@ToString
public class EmailNotificationDto {
    private String email;
    private String subject;
    private String message;
}
