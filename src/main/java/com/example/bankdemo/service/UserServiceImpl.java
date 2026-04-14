package com.example.bankdemo.service;

import com.example.bankdemo.dao.UserRepository;
import com.example.bankdemo.entity.Account;
import com.example.bankdemo.entity.User;
import com.example.bankdemo.exception.UserAlreadyExistsException;
import com.example.bankdemo.exception.UserNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public User createUser(String login, Account initialAccount) {

        if (existsByLogin(login)) {
            throw new UserAlreadyExistsException(login);
        }
        User user = new User(login);
        if (initialAccount != null) {
            user.addAccount(initialAccount);
        }
        userRepository.save(user);
        return user;

    }

    private boolean existsByLogin(String login) {
        return !userRepository.findByLoginQ(login).isEmpty();
    }

    @Override
    public User findById(long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public Collection<User> findAll() {
        return userRepository.findAll();
    }
}
