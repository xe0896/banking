package com.sanim.banking.dto;

import com.sanim.banking.domain.Money;
import com.sanim.banking.domain.transaction.TransactionStatus;

import java.time.Instant;
import java.util.UUID;

public record TransactionResponse (UUID id, TransactionStatus type, Instant completedAt, Money newAmount) {}
