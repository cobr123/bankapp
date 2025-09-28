package com.example.bankapp.notifications.model;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
@ToString
public class EmailNotificationDto {
    private String email;
    private String subject;
    private String message;
}
