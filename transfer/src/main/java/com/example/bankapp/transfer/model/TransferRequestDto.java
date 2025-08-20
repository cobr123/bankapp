package com.example.bankapp.transfer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class TransferRequestDto {
    @JsonProperty("from_currency")
    private final Currency fromCurrency;
    @JsonProperty("to_currency")
    private final Currency toCurrency;
    private final BigDecimal value;
    @JsonProperty("to_login")
    private final String toLogin;
}
