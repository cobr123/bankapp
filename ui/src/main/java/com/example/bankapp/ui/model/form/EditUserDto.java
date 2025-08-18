package com.example.bankapp.ui.model.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class EditUserDto {
    private String name;
    private String email;
    private LocalDate birthdate;
}