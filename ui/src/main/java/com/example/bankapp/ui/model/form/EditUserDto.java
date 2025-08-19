package com.example.bankapp.ui.model.form;

import com.example.bankapp.ui.model.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
public class EditUserDto {
    private String name;
    private String email;
    private LocalDate birthdate;
    private Set<Currency> account;
}