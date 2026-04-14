package com.example.bankdemo.service;

import com.example.bankdemo.entity.Account;
import com.example.bankdemo.entity.User;

import java.util.Collection;

public interface UserService {
    Collection<User> findAll();

    User findById(long id);

    User createUser(String login, Account initialAccount);
}
