package com.sanim.banking.controller;

import com.sanim.banking.domain.Money;
import com.sanim.banking.domain.transaction.Transaction;
import com.sanim.banking.dto.TransactionResponse;
import com.sanim.banking.dto.WithdrawRequest;
import com.sanim.banking.service.AccountService;
import com.sanim.banking.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactions;
    private final AccountService accounts;

    // @RequestHeader("idom-key") would look at the header and find an idom-key entry, the reason
    // why it is in the header is because it is metadata
    @PostMapping("/withdraw/{id}")
    ResponseEntity<TransactionResponse> withdraw(@RequestHeader("idom-key") String key, @RequestBody WithdrawRequest req) {
        Transaction transaction = transactions.withdraw(req.accountId(), req.amount(), req.userId(), key);
        Money newBalance = accounts.getBalance(req.accountId());
        return ResponseEntity.status(201).body(toDto(transaction, newBalance));
    }

    private TransactionResponse toDto(Transaction transaction, Money newBalance) {
        return new TransactionResponse(transaction.getId(), transaction.getStatus(), transaction.getCompletedAt(), newBalance);
    }
}
