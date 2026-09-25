package com.example.demo.user.dto.updateRequest;

import com.example.demo.teacher.dto.TeacherRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record TeacherUpdateRequest(
        @Valid @NotNull UserUpdateRequest userRequest,
        @Valid @NotNull TeacherRequest teacherRequest
) {
}
