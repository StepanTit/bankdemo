package com.example.bankdemo.dao;

import com.example.bankdemo.entity.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.id = :id")
    Optional<Account> findByIdForUpdate(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Account a SET a.moneyAmount = a.moneyAmount + :amount WHERE a.id = :id")
    int addToBalance(@Param("id") long id, @Param("amount") BigDecimal amount);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Account a SET a.moneyAmount = a.moneyAmount - :amount WHERE a.id = :id AND a.moneyAmount >= :amount")
    int subtractFromBalanceIfSufficient(@Param("id") long id, @Param("amount") BigDecimal amount);
}
