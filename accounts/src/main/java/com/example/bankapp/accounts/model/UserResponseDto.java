package com.example.bankapp.accounts.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private String login;
    private String password;
    private String name;
    private String email;
    private LocalDate dateOfBirth;
    private BigDecimal balance;
} 