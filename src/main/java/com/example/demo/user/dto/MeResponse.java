package com.example.demo.user.dto;

public record MeResponse(
        String role,
        Long entityId,
        String name
) {
}
