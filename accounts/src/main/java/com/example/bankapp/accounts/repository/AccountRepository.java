package com.example.bankapp.accounts.repository;

import com.example.bankapp.accounts.model.Account;
import com.example.bankapp.accounts.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUserId(Long userId);

    Optional<Account> findByUserIdAndCurrency(Long userId, Currency currency);
}