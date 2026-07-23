package com.sanim.banking.dto;

import java.util.UUID;

public record LoginResponse(String displayName, UUID id, String token) {}
