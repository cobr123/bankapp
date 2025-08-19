package com.example.bankapp.accounts.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@AllArgsConstructor
@Builder
public class EditUserRequestDto {
    private String name;
    private String email;
    private LocalDate birthdate;
    private Set<Currency> accounts;
}