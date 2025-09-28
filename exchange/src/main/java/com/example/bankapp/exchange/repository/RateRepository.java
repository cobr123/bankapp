package com.example.bankapp.exchange.repository;

import com.example.bankapp.exchange.model.Currency;
import com.example.bankapp.exchange.model.Rate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RateRepository extends JpaRepository<Rate, Long> {
    void deleteByCurrency(Currency currency);
}