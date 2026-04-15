package com.example.bankdemo.service;

import com.example.bankdemo.dao.AccountRepository;
import com.example.bankdemo.entity.Account;
import com.example.bankdemo.entity.User;
import com.example.bankdemo.exception.AccountNotFoundException;
import com.example.bankdemo.exception.CannotCloseLastAccountException;
import com.example.bankdemo.exception.InsufficientFundsException;
import com.example.bankdemo.exception.InvalidAmountException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService;

    private final BigDecimal defaultAmount;
    private final BigDecimal transferCommissionPercent;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository,
                              UserService userService,
                              @Value("${account.default-amount}") BigDecimal defaultAmount,
                              @Value("${account.transfer-commission}") BigDecimal transferCommissionPercent) {
        this.accountRepository = accountRepository;
        this.userService = userService;
        this.defaultAmount = defaultAmount;
        this.transferCommissionPercent = transferCommissionPercent;
    }

    @Override
    @Transactional
    public Account createAccount(long userId) {
        User user = userService.findById(userId);
        Account account = new Account(user, defaultAmount);
        user.addAccount(account);
        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public Account deposit(long accountId, BigDecimal amount) {
        validatePositiveAmount(amount);
        if (accountRepository.addToBalance(accountId, amount) == 0) {
            throw new AccountNotFoundException(accountId);
        }
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @Override
    @Transactional
    public Account withdraw(long accountId, BigDecimal amount) {
        validatePositiveAmount(amount);
        if (accountRepository.subtractFromBalanceIfSufficient(accountId, amount) == 0) {
            BigDecimal current = accountRepository.findById(accountId)
                    .map(Account::getMoneyAmount)
                    .orElseThrow(() -> new AccountNotFoundException(accountId));
            throw new InsufficientFundsException(accountId, current, amount);
        }
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @Override
    @Transactional
    public TransferResult transfer(long sourceAccountId, long targetAccountId, BigDecimal amount) {
        validatePositiveAmount(amount);
        if (sourceAccountId == targetAccountId) {
            throw new IllegalArgumentException("Source and target account IDs must be different");
        }

        long lowId = Math.min(sourceAccountId, targetAccountId);
        long highId = Math.max(sourceAccountId, targetAccountId);
        Account lockedLow = getAccountForUpdate(lowId);
        Account lockedHigh = getAccountForUpdate(highId);
        Account source = sourceAccountId == lowId ? lockedLow : lockedHigh;
        Account target = targetAccountId == lowId ? lockedLow : lockedHigh;

        boolean sameUser = source.getUserId() == target.getUserId();

        if (sameUser) {
            withdrawFromLockedAccount(source, amount);
            depositToLockedAccount(target, amount);
            return new TransferResult(source, target);
        }

        BigDecimal commission = amount
                .multiply(transferCommissionPercent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal totalToWithdraw = amount.add(commission);

        BigDecimal current = source.getMoneyAmount();
        if (current.compareTo(totalToWithdraw) < 0) {
            throw new InsufficientFundsException(sourceAccountId, current, totalToWithdraw);
        }

        source.setMoneyAmount(current.subtract(totalToWithdraw));
        target.setMoneyAmount(target.getMoneyAmount().add(amount));
        return new TransferResult(source, target);
    }

    @Override
    @Transactional
    public void closeAccount(long accountId) {
        Account accountPreview = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        User user = userService.findById(accountPreview.getUserId());

        if (user.getAccountList().size() <= 1) {
            throw new CannotCloseLastAccountException(user.getId(), accountId);
        }

        Account recipientPreview = user.getAccountList().get(0);
        if (recipientPreview.getId() == accountId) {
            recipientPreview = user.getAccountList().get(1);
        }
        long recipientId = recipientPreview.getId();

        long lowId = Math.min(accountId, recipientId);
        long highId = Math.max(accountId, recipientId);
        Account lockedLow = getAccountForUpdate(lowId);
        Account lockedHigh = getAccountForUpdate(highId);
        Account account = accountId == lowId ? lockedLow : lockedHigh;
        Account recipient = recipientId == lowId ? lockedLow : lockedHigh;

        user = userService.findById(account.getUserId());

        BigDecimal balanceToTransfer = account.getMoneyAmount();
        if (balanceToTransfer.compareTo(BigDecimal.ZERO) > 0) {
            recipient.setMoneyAmount(recipient.getMoneyAmount().add(balanceToTransfer));
        }

        user.removeAccount(account);
        accountRepository.delete(account);
    }

    private Account getAccountForUpdate(long accountId) {
        return accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    private void withdrawFromLockedAccount(Account account, BigDecimal amount) {
        validatePositiveAmount(amount);
        long accountId = account.getId();
        BigDecimal current = account.getMoneyAmount();
        if (current.compareTo(amount) < 0) {
            throw new InsufficientFundsException(accountId, current, amount);
        }
        account.setMoneyAmount(current.subtract(amount));
    }

    private void depositToLockedAccount(Account account, BigDecimal amount) {
        validatePositiveAmount(amount);
        account.setMoneyAmount(account.getMoneyAmount().add(amount));
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(amount);
        }
    }
}
