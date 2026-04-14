package com.example.bankdemo.service;

import com.example.bankdemo.entity.Account;

import java.math.BigDecimal;

public interface AccountService {

    Account createAccount(long userId);

    Account deposit(long accountId, BigDecimal amount);

    void withdraw(long accountId, BigDecimal amount);

    void transfer(long sourceAccountId, long targetAccountId, BigDecimal amount);

    void closeAccount(long accountId);
}
