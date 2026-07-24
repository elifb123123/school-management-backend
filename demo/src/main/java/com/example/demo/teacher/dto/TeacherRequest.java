package com.example.demo.teacher.dto;

import jakarta.validation.constraints.NotBlank;

public record TeacherRequest(
        @NotBlank
        String name,
        @NotBlank
        String schoolName
) {
}
