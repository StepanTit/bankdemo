package com.example.bankdemo.service;

import com.example.bankdemo.entity.Account;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public interface AccountService {

    Account createAccount(long userId);

    Account findById(long accountId);

    Account deposit(long accountId, @NotNull @Positive BigDecimal amount);

    Account withdraw(long accountId, @NotNull @Positive BigDecimal amount);

    TransferResult transfer(long sourceAccountId,
                              long targetAccountId,
                              @NotNull @Positive BigDecimal amount);

    Account depositOptimistic(long accountId, @NotNull @Positive BigDecimal amount);

    Account withdrawOptimistic(long accountId, @NotNull @Positive BigDecimal amount);

    TransferResult transferOptimistic(long sourceAccountId,
                                      long targetAccountId,
                                      @NotNull @Positive BigDecimal amount);

    void closeAccount(long accountId);
}
