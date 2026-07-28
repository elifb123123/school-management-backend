package com.example.demo.school.dto;

import jakarta.validation.constraints.NotBlank;

public record SchoolRequest(
        @NotBlank
        String schoolName) {
}
