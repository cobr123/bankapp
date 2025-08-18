package com.example.bankapp.ui.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class EditUserRequestDto {
    private String login;
    private String name;
    private String email;
    private LocalDate birthdate;
}