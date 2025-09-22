package com.example.bankapp.ui.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder
public class RateUiResponseDto {
    private String title;
    private String name;
    private BigDecimal value;
} 