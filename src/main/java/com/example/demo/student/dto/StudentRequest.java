package com.example.demo.student.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record StudentRequest(
        @NotNull(message = "Doğum tarihi girin")
        LocalDate dateOfBirth,
        @NotNull(message = "Okul id girin")
        Long schoolId
) {
}