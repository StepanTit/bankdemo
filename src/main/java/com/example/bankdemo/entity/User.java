package com.example.bankdemo.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    private String login;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Account> accounts = new ArrayList<>();

    public User(String login) {
        this.login = Objects.requireNonNull(login, "login must not be null");
    }

    public long getId() {
        if (id == null) {
            throw new IllegalStateException("User is not persisted yet");
        }
        return id;
    }

    public String getLogin() {
        return login;
    }

    public List<Account> getAccountList() {
        return Collections.unmodifiableList(accounts);
    }

    public void addAccount(Account account) {
        Objects.requireNonNull(account, "account must not be null");
        accounts.add(account);
        account.bindUser(this);
    }

    public void removeAccount(Account account) {
        accounts.remove(account);
        account.bindUser(null);
    }

    @Override
    public String toString() {
        return "User{id=" + id +
                ", login='" + login + '\'' +
                ", accountList=" + accounts +
                '}';
    }
}
