package com.example.bankapp.accounts.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class AccountChangeRequestDto {
    private final Currency currency;
    private final BigDecimal before;
    private final BigDecimal after;
    private final String login;
}
