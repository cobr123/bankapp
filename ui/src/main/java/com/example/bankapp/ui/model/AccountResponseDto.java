package com.example.bankapp.ui.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder
public class AccountResponseDto {
    private Currency currency;
    private BigDecimal value;
    private boolean exists;

    public boolean isExists() {
        return exists;
    }
}
