package com.example.demo.user.dto.registration;

import com.example.demo.teacher.dto.TeacherRequest;
import com.example.demo.user.dto.UserRequest;
import jakarta.validation.constraints.NotNull;

public record TeacherRegistrationRequest(
        @NotNull(message = "User request cannot be null")
        UserRequest userRequest,
        @NotNull(message = "Teacher request cannot be null")
        TeacherRequest teacherRequest
) {
}
