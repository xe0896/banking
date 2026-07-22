package com.sanim.banking.dto;

import com.sanim.banking.domain.Money;

import java.util.UUID;

public record WithdrawRequest(UUID accountId, Money amount, UUID userId) {}
