package com.example.bankdemo.rest.dto;

import com.example.bankdemo.entity.Account;

import java.math.BigDecimal;

public record AccountResponse(long id, long userId, BigDecimal moneyAmount) {

    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getUserId(),
                account.getMoneyAmount());
    }
}
