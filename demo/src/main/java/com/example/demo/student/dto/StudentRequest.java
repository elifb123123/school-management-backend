package com.example.demo.student.dto;

import java.time.LocalDate;

public record StudentRequest(
        String name,
        LocalDate dateOfBirth,
        String schoolName
) {
}