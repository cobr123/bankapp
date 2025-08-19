package com.example.bankapp.accounts.repository;

import com.example.bankapp.accounts.model.Account;
import com.example.bankapp.accounts.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUserId(Long userId);

    Optional<Account> findByUserIdAndCurrency(Long userId, Currency currency);
}