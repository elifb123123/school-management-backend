package com.example.demo.user.dto.updateRequest;

import com.example.demo.student.dto.StudentRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record StudentUpdateRequest(
        @Valid @NotNull UserUpdateRequest userRequest,
        @Valid @NotNull StudentRequest studentRequest
) {
}
