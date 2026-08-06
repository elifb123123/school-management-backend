package com.example.demo.school.dto;

import jakarta.validation.constraints.NotBlank;

public record SchoolRequest(
        @NotBlank(message = "School name is required.")
        String schoolName,
        @NotBlank(message = "School address is required.")
        String address
) {
}
