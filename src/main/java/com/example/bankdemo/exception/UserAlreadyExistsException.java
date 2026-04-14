package com.example.bankdemo.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super("User with login '" + message + "' already exists");
    }
}
