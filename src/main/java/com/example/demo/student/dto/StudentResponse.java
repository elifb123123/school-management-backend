package com.example.demo.student.dto;

import java.time.LocalDate;

public record StudentResponse(
        Long id,
        String name,
        String email,
        LocalDate dateOfBirth,
        Integer age,
        Long schoolId
) {
}