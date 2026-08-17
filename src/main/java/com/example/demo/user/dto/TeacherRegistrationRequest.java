package com.example.demo.user.dto;

import com.example.demo.teacher.dto.TeacherRequest;
import jakarta.validation.constraints.NotNull;

public record TeacherRegistrationRequest(
        @NotNull(message = "User request cannot be null")
        UserRequest userRequest,
        @NotNull(message = "Teacher request cannot be null")
        TeacherRequest teacherRequest
) {
}
