package com.sanim.banking.controller;

import com.sanim.banking.domain.account.Account;
import com.sanim.banking.dto.AccountResponse;
import com.sanim.banking.dto.OpenAccountRequest;
import com.sanim.banking.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController // Returns JSON data, @Controller would return web pages like index.html if we returned "index"
@RequestMapping("/api/accounts") // Endpoint
@RequiredArgsConstructor // Required for the inst of AccountService injection
public class AccountController {
    private final AccountService accounts;

    // POST would give it data and it would do something with it, could also return what it created like it is now
    @PostMapping
    ResponseEntity<AccountResponse> open(@RequestBody OpenAccountRequest req, Authentication auth) {
        System.out.println("Open account reached");
        UUID callerId = UUID.fromString(auth.getName());
        var acc = accounts.openAccount(callerId, req.type(), req.currencyCode());
        // AccountResponse is a record so Spring can parse it for us using its getters
        return ResponseEntity.status(HttpStatus.CREATED).body(new AccountResponse(
                acc.getId(),
                acc.getCurrencyCode(),
                acc.getType()));
    }

    @PostMapping("/{accountId}/freeze")
    ResponseEntity<AccountResponse> freeze(@PathVariable UUID accountId, Authentication auth) {
        UUID callerId = UUID.fromString(auth.getName());

        Account acc = accounts.freeze(accountId, callerId);
        return ResponseEntity.status(HttpStatus.OK).body(new AccountResponse(
                acc.getId(),
                acc.getCurrencyCode(),
                acc.getType()
        ));
    }

    @PostMapping("/{accountId}/unfreeze")
    ResponseEntity<AccountResponse> unfreeze(@PathVariable UUID accountId, Authentication auth) {
        UUID callerId = UUID.fromString(auth.getName());

        Account acc = accounts.unfreeze(accountId, callerId);
        return ResponseEntity.status(HttpStatus.OK).body(new AccountResponse(
                acc.getId(),
                acc.getCurrencyCode(),
                acc.getType()
        ));
    }

    // /api/accounts/{accountid}/close closes an account. Rejected if the balance is non-zero
    @PostMapping("/{accountId}/close")
    ResponseEntity<AccountResponse> close(@PathVariable UUID accountId, Authentication auth) {
        if(!accounts.getBalance(accountId).isZero()) {
            // Frontend should infer balance is zero via status code
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        }

        UUID callerId = UUID.fromString(auth.getName());

        Account acc = accounts.close(accountId, callerId);

        return ResponseEntity.status(HttpStatus.OK).body(new AccountResponse(
                acc.getId(),
                acc.getCurrencyCode(),
                acc.getType()
        ));
    }


}
