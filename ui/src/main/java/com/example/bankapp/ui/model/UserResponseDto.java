package com.example.bankapp.ui.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private String login;
    private String password;
    private String name;
    private String email;
    private LocalDate dateOfBirth;
    private List<AccountResponseDto> accounts;
} 