package com.example.demo.teacher.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TeacherRequest(
        @NotBlank(message = "Teacher name is required")
        String name,
        @NotBlank(message = "Teacher email is required")
        @Email(message = "Invalid email format")
        String email,
        @NotNull(message = "School ID is required")
        Long schoolId

) {
}
