package com.example.bankdemo.dao;

import com.example.bankdemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.login = ?1")
    List<User> findByLoginQ(String login);

    @Query("select distinct u from User u left join fetch u.accounts order by u.id")
    List<User> findAllUsers();
}
