package com.example.demo.auth.dto;

import jakarta.validation.constraints.NotNull;

public record RefreshRequest(
        @NotNull String refreshToken) {
}
