package com.sanim.banking.controller;

import com.sanim.banking.domain.account.Account;
import com.sanim.banking.dto.AccountResponse;
import com.sanim.banking.dto.OpenAccountRequest;
import com.sanim.banking.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // Returns JSON data, @Controller would return web pages like index.html if we returned "index"
@RequestMapping("/api/accounts") // Endpoint
@RequiredArgsConstructor // Required for the inst of AccountService injection
public class AccountController {
    private final AccountService accounts;
    public AccountResponse toDto(Account acc) {
        return new AccountResponse(
                acc.getId(),
                acc.getCurrencyCode(),
                acc.getType()
        );
    }

    // POST would give it data and it would do something with it, could also return what it created like it is now
    @PostMapping
    ResponseEntity<AccountResponse> open(@RequestBody OpenAccountRequest req) {
        var acc = accounts.openAccount(req.userId(), req.type(), req.currencyCode());
        // AccountResponse is a record so Spring can parse it for us using its getters
        return ResponseEntity.status(201).body(toDto(acc));
    } 
}
