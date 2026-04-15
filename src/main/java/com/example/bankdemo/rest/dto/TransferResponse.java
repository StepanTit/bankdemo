package com.example.bankdemo.rest.dto;

import com.example.bankdemo.entity.Account;
import com.example.bankdemo.service.TransferResult;

public record TransferResponse(AccountResponse sourceAccount, AccountResponse targetAccount) {

    public static TransferResponse from(TransferResult result) {
        return from(result.sourceAccount(), result.targetAccount());
    }

    public static TransferResponse from(Account source, Account target) {
        return new TransferResponse(AccountResponse.from(source), AccountResponse.from(target));
    }
}
