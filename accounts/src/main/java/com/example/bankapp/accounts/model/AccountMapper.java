package com.example.bankapp.accounts.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AccountMapper {

    public UserResponseDto toDto(List<Account> accounts, UserResponseDto userResponseDto) {
        var map = accounts.stream().collect(Collectors.toMap(Account::getCurrency, v -> v));
        var accountResponseDtos = new ArrayList<AccountResponseDto>();
        for (Currency currency : Currency.values()) {
            var account = map.get(currency);
            var accountDto = AccountResponseDto.builder().currency(currency);
            if (account != null) {
                accountDto.value(account.getValue());
            }
            accountResponseDtos.add(accountDto.build());
        }
        userResponseDto.setAccounts(accountResponseDtos);
        return userResponseDto;
    }
}
