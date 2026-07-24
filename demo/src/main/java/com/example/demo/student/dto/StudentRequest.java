package com.example.demo.student.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record StudentRequest(
        @NotBlank(message = "İsim girin")
        @Size(min = 3, max = 20)
        String name,
        @NotBlank(message = "mail girin")
        @Email(message = "Geçersiz email formatı")
        String email,
        @NotBlank(message = "Doğum tarihi girin")
        LocalDate dateOfBirth,
        @NotBlank(message = "Okul adı girin")
        String schoolName
) {
}