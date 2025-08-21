package com.example.bankapp.exchange_generator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder
public class UpdateRateRequestDto {
    private Currency currency;
    private BigDecimal value;
}
