package com.example.bankdemo.exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(long accountId, BigDecimal currentAmount, BigDecimal attemptedWithdraw) {
        super("No such money to withdraw from account: id=" + accountId
                + ", moneyAmount=" + currentAmount
                + ", attemptedWithdraw=" + attemptedWithdraw);
    }
}
