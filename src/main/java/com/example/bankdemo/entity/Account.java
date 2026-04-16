package com.example.bankdemo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "accounts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Getter
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal moneyAmount;

    public Account(User user, BigDecimal moneyAmount) {
        this.user = Objects.requireNonNull(user, "user must not be null");
        this.moneyAmount = Objects.requireNonNull(moneyAmount, "moneyAmount must not be null");
    }

    void bindUser(User user) {
        this.user = user;
    }

    public long getId() {
        if (id == null) {
            throw new IllegalStateException("Account is not persisted yet");
        }
        return id;
    }

    public long getUserId() {
        return user.getId();
    }

    public void setMoneyAmount(BigDecimal moneyAmount) {
        this.moneyAmount = Objects.requireNonNull(moneyAmount, "moneyAmount must not be null");
    }

    @Override
    public String toString() {
        return "Account{id=" + id +
                ", userId=" + (user != null ? user.getId() : null) +
                ", moneyAmount=" + moneyAmount +
                '}';
    }
}
