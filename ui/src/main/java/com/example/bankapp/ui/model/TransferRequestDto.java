package com.example.bankapp.ui.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.beans.ConstructorProperties;
import java.math.BigDecimal;

@Getter
@Builder
public class TransferRequestDto {
    @JsonProperty("from_currency")
    private final Currency fromCurrency;
    @JsonProperty("to_currency")
    private final Currency toCurrency;
    private final BigDecimal value;
    @JsonProperty("to_login")
    private final String toLogin;

    @ConstructorProperties({"from_currency", "to_currency", "value", "to_login"})
    public TransferRequestDto(Currency fromCurrency, Currency toCurrency, BigDecimal value, String toLogin) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.value = value;
        this.toLogin = toLogin;
    }
}
