package com.example.bankapp.ui.model;

public enum Currency {
    RUB, USD, CNY;

    public String getTitle() {
        return name();
    }
}
