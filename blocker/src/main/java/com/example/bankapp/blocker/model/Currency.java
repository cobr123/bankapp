package com.example.bankapp.blocker.model;

public enum Currency {
    RUB, USD, CNY;

    public String getTitle() {
        return name();
    }
}
