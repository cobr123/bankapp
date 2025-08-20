package com.example.bankapp.transfer.model;

public enum Currency {
    RUB, USD, CNY;

    public String getTitle() {
        return name();
    }
}
