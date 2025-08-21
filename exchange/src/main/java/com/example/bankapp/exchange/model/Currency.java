package com.example.bankapp.exchange.model;

public enum Currency {
    RUB, USD, CNY;

    public String getTitle() {
        return name();
    }
}
