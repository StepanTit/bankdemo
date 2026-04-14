package com.example.bankdemo.exception;

public class CannotCloseLastAccountException extends RuntimeException {

    public CannotCloseLastAccountException(long userId, long accountId) {
        super("Cannot close the only account for userId=" + userId + ", accountId=" + accountId);
    }
}
