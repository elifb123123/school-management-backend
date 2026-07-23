package com.example.demo.student.dto;

import java.time.LocalDate;

public record StudentResponse(
        Long id,
        String name,
        LocalDate dateOfBirth,
        Integer age,
        String schoolName
) {
}