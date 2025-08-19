package com.example.bankapp.accounts.service;

import com.example.bankapp.accounts.model.Account;
import com.example.bankapp.accounts.model.Currency;
import com.example.bankapp.accounts.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;

    @Caching(
            evict = {@CacheEvict(value = "user_accounts", key = "#account.getUserId()")},
            put = {@CachePut(value = "accounts", key = "#account.getUserId() + '_' + #account.getCurrency().name()")}
    )
    public Account insert(Account account) {
        return accountRepository.save(account);
    }

    @Cacheable(value = "user_accounts", key = "#userId")
    public List<Account> findByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    @Cacheable(value = "accounts", key = "#userId + '_' + #currency.name()", unless = "#result == null")
    public Optional<Account> findByUserIdAndCurrency(Long userId, Currency currency) {
        return accountRepository.findByUserIdAndCurrency(userId, currency);
    }

    @Caching(
            evict = {@CacheEvict(value = "user_accounts", key = "#account.getUserId()")},
            put = {@CachePut(value = "accounts", key = "#account.getUserId() + '_' + #account.getCurrency().name()")}
    )
    public Account update(Account account) {
        return accountRepository.save(account);
    }

    @Caching(evict = {
            @CacheEvict(value = "user_accounts", key = "#account.getUserId()"),
            @CacheEvict(value = "accounts", key = "#account.getUserId() + '_' + #account.getCurrency().name()")
    })
    public void delete(Account account) {
        accountRepository.delete(account);
    }

}