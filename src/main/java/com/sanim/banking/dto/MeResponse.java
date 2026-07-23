package com.sanim.banking.dto;

import com.sanim.banking.domain.user.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record MeResponse(UUID id, String displayName, UserStatus status, Instant createdAt) {}
