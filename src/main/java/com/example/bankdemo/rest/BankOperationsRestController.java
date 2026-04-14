package com.example.bankdemo.rest;

import com.example.bankdemo.entity.Account;
import com.example.bankdemo.entity.User;
import com.example.bankdemo.rest.dto.AccountAmountOperationRequest;
import com.example.bankdemo.rest.dto.AccountCloseRequest;
import com.example.bankdemo.rest.dto.AccountCreateRequest;
import com.example.bankdemo.rest.dto.AccountResponse;
import com.example.bankdemo.rest.dto.TransferRequest;
import com.example.bankdemo.rest.dto.UserCreateRequest;
import com.example.bankdemo.rest.dto.UserResponse;
import com.example.bankdemo.service.AccountService;
import com.example.bankdemo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/operations")
public class BankOperationsRestController {

    private final UserService userService;
    private final AccountService accountService;

    public BankOperationsRestController(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    @PostMapping("/user-create")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse userCreate(@Valid @RequestBody UserCreateRequest request) {
        User user = userService.createUser(request.login(), null);
        accountService.createAccount(user.getId());
        User refreshed = userService.findById(user.getId());
        return UserResponse.from(refreshed);
    }

    @GetMapping("/show-all-users")
    public List<UserResponse> showAllUsers() {
        return userService.findAll().stream().map(UserResponse::from).toList();
    }

    @PostMapping("/account-create")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse accountCreate(@Valid @RequestBody AccountCreateRequest request) {
        Account account = accountService.createAccount(request.userId());
        return AccountResponse.from(account);
    }

    @PostMapping("/account-close")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void accountClose(@Valid @RequestBody AccountCloseRequest request) {
        accountService.closeAccount(request.accountId());
    }

    @PostMapping("/account-deposit")
    public AccountResponse accountDeposit(@Valid @RequestBody AccountAmountOperationRequest request) {
        Account account = accountService.deposit(request.accountId(), request.amount());
        return AccountResponse.from(account);
    }

    @PostMapping("/account-transfer")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void accountTransfer(@Valid @RequestBody TransferRequest request) {
        accountService.transfer(
                request.sourceAccountId(),
                request.targetAccountId(),
                request.amount());
    }

    @PostMapping("/account-withdraw")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void accountWithdraw(@Valid @RequestBody AccountAmountOperationRequest request) {
        accountService.withdraw(request.accountId(), request.amount());
    }
}
