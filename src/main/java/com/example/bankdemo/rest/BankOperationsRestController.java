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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BankOperationsRestController {

    private final UserService userService;
    private final AccountService accountService;

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        User user = userService.createUser(request.login(), null);
        accountService.createAccount(user.getId());
        User refreshed = userService.findById(user.getId());
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(refreshed.getId())
                        .toUri())
                .body(UserResponse.from(refreshed));
    }

    @GetMapping("/users")
    public List<UserResponse> getUsers() {
        return userService.findAll().stream().map(UserResponse::from).toList();
    }

    @GetMapping("/users/{userId}")
    public UserResponse getUser(@PathVariable long userId) {
        return UserResponse.from(userService.findById(userId));
    }

    @PostMapping("/accounts")
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountCreateRequest request) {
        Account account = accountService.createAccount(request.userId());
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(account.getId())
                        .toUri())
                .body(AccountResponse.from(account));
    }

    @GetMapping("/accounts")
    public List<AccountResponse> getAccounts() {
        return userService.findAll().stream()
                .flatMap(user -> user.getAccountList().stream())
                .map(AccountResponse::from)
                .toList();
    }

    @GetMapping("/accounts/{accountId}")
    public AccountResponse getAccount(@PathVariable long accountId) {
        return AccountResponse.from(accountService.findById(accountId));
    }

    @DeleteMapping("/accounts/{accountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void closeAccount(@PathVariable long accountId) {
        accountService.closeAccount(accountId);
    }

    @PostMapping("/v1/accounts/{accountId}/deposit")
    public AccountResponse depositV1(@PathVariable long accountId,
                                     @Valid @RequestBody AccountAmountOperationRequest request) {
        Account account = accountService.deposit(accountId, request.amount());
        return AccountResponse.from(account);
    }

    @PostMapping("/v2/accounts/{accountId}/deposit")
    public AccountResponse depositV2(@PathVariable long accountId,
                                     @Valid @RequestBody AccountAmountOperationRequest request) {
        Account account = accountService.depositOptimistic(accountId, request.amount());
        return AccountResponse.from(account);
    }

    @PostMapping("/v1/accounts/{accountId}/transfer")
    public TransferResponse transferV1(@PathVariable long accountId,
                                       @Valid @RequestBody TransferRequest request) {
        return TransferResponse.from(accountService.transfer(
                accountId,
                request.targetAccountId(),
                request.amount()));
    }

    @PostMapping("/v2/accounts/{accountId}/transfer")
    public TransferResponse transferV2(@PathVariable long accountId,
                                       @Valid @RequestBody TransferRequest request) {
        return TransferResponse.from(accountService.transferOptimistic(
                accountId,
                request.targetAccountId(),
                request.amount()));
    }

    @PostMapping("/v1/accounts/{accountId}/withdraw")
    public AccountResponse withdrawV1(@PathVariable long accountId,
                                      @Valid @RequestBody AccountAmountOperationRequest request) {
        Account account = accountService.withdraw(accountId, request.amount());
        return AccountResponse.from(account);
    }

    @PostMapping("/v2/accounts/{accountId}/withdraw")
    public AccountResponse withdrawV2(@PathVariable long accountId,
                                      @Valid @RequestBody AccountAmountOperationRequest request) {
        Account account = accountService.withdrawOptimistic(accountId, request.amount());
        return AccountResponse.from(account);
    }
}
