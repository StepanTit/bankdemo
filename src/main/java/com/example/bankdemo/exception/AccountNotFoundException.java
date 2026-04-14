package com.example.bankdemo.exception;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(long accountId) {
        super("Account with id=" + accountId + " not found");
    }
}
