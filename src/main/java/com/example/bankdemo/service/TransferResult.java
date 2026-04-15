package com.example.bankdemo.service;

import com.example.bankdemo.entity.Account;

public record TransferResult(Account sourceAccount, Account targetAccount) {
}
