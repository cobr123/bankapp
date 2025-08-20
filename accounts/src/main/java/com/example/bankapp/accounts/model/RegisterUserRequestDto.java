package com.example.bankapp.accounts.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
public class RegisterUserRequestDto {
    private String login;
    private String password;
    private String name;
    private LocalDate birthdate;
}