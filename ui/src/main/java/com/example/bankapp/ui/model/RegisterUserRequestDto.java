package com.example.bankapp.ui.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class RegisterUserRequestDto {
    private String login;
    private String password;
    private String name;
    private LocalDate birthdate;
}