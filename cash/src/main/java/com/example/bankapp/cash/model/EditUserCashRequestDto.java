package com.example.bankapp.cash.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class EditUserCashRequestDto {
    private String login;
    private Currency currency;
    private BigDecimal value;
    private EditUserCashAction action;
}