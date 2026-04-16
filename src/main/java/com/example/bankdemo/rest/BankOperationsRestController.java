package com.example.bankdemo.rest;

import com.example.bankdemo.entity.Account;
import com.example.bankdemo.entity.User;
import com.example.bankdemo.rest.dto.AccountAmountOperationRequest;
import com.example.bankdemo.rest.dto.AccountCreateRequest;
import com.example.bankdemo.rest.dto.AccountResponse;
import com.example.bankdemo.rest.dto.TransferRequest;
import com.example.bankdemo.rest.dto.TransferResponse;
import com.example.bankdemo.rest.dto.UserCreateRequest;
import com.example.bankdemo.rest.dto.UserResponse;
import com.example.bankdemo.service.AccountService;
import com.example.bankdemo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BankOperationsRestController {

    private final UserService userService;
    private final AccountService accountService;

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody UserCreateRequest request) {
        User user = userService.createUser(request.login(), null);
        accountService.createAccount(user.getId());
        User refreshed = userService.findById(user.getId());
        return UserResponse.from(refreshed);
    }

    @GetMapping("/users")
    public List<UserResponse> getUsers() {
        return userService.findAll().stream().map(UserResponse::from).toList();
    }

    @PostMapping("/accounts")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse createAccount(@Valid @RequestBody AccountCreateRequest request) {
        Account account = accountService.createAccount(request.userId());
        return AccountResponse.from(account);
    }

    @GetMapping("/accounts")
    public List<AccountResponse> getAccounts() {
        return userService.findAll().stream()
                .flatMap(user -> user.getAccountList().stream())
                .map(AccountResponse::from)
                .toList();
    }

    @DeleteMapping("/accounts/{accountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void closeAccount(@PathVariable long accountId) {
        accountService.closeAccount(accountId);
    }

    @PostMapping("/accounts/{accountId}/deposit")
    public AccountResponse deposit(@PathVariable long accountId,
                                   @Valid @RequestBody AccountAmountOperationRequest request) {
        Account account = accountService.deposit(accountId, request.amount());
        return AccountResponse.from(account);
    }

    @PostMapping("/accounts/{accountId}/transfer")
    public TransferResponse transfer(@PathVariable long accountId,
                                     @Valid @RequestBody TransferRequest request) {
        return TransferResponse.from(accountService.transfer(
                accountId,
                request.targetAccountId(),
                request.amount()));
    }

    @PostMapping("/accounts/{accountId}/withdraw")
    public AccountResponse withdraw(@PathVariable long accountId,
                                    @Valid @RequestBody AccountAmountOperationRequest request) {
        Account account = accountService.withdraw(accountId, request.amount());
        return AccountResponse.from(account);
    }
}
