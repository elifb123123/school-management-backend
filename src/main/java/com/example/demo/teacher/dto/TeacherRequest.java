package com.example.demo.teacher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TeacherRequest(
        @NotBlank(message = "Teacher name is required")
        String name,
        @NotNull(message = "School ID is required")
        Long schoolId
) {
}
