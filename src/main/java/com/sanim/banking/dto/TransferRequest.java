package com.sanim.banking.dto;

import com.sanim.banking.domain.Money;

import java.util.UUID;

public record TransferRequest(UUID fromAccountId, UUID toAccountId, Money amount) {}
