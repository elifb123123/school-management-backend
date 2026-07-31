package com.example.demo.teacher.dto;

import jakarta.validation.constraints.NotBlank;

public record TeacherRequest(
        @NotBlank(message = "Teacher name is required")
        String name,
        @NotBlank(message = "School ID is required")
        Long schoolId
) {
}
