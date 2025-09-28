package com.example.bankapp.exchange.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class UpdateRateRequestDto {
    private Currency currency;
    private BigDecimal value;
}
