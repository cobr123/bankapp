package com.example.bankapp.accounts;

import com.example.bankapp.accounts.model.AccountMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AccountsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountsApplication.class, args);
    }

    @Bean
    AccountMapper accountMapper() {
        return new AccountMapper();
    }
}
