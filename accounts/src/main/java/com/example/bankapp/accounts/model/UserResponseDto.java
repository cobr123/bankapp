package com.example.bankapp.accounts.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private String login;
    private String password;
    private String name;
    private String email;
    private LocalDate birthdate;
    private BigDecimal balance;
    private List<AccountResponseDto> accounts;
}