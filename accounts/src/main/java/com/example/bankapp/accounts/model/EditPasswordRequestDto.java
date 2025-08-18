package com.example.bankapp.accounts.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class EditPasswordRequestDto {
    private String password;
}