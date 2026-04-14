package com.example.bankdemo.rest.dto;

import com.example.bankdemo.entity.User;

import java.util.List;

public record UserResponse(Long id, String login, List<AccountResponse> accounts) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getLogin(),
                user.getAccountList().stream().map(AccountResponse::from).toList());
    }
}
