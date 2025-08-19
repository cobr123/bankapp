package com.example.bankapp.ui.model.form;

import com.example.bankapp.ui.model.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class EditUserCashDto {
    private Currency currency;
    private BigDecimal value;
    private EditUserCashAction action;
}