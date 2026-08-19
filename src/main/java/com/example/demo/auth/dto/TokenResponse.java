package com.example.demo.auth.dto;

import jakarta.validation.constraints.NotNull;

public record TokenResponse(
        @NotNull String accessToken,
        @NotNull String refreshToken
) {
}
