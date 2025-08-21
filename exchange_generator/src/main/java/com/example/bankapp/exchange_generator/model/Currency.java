package com.example.bankapp.exchange_generator.model;

public enum Currency {
    RUB, USD, CNY;

    public String getTitle() {
        return name();
    }
}
