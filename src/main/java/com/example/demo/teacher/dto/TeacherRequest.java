package com.example.demo.teacher.dto;

import com.example.demo.teacher.persistence.Branch;
import jakarta.validation.constraints.NotNull;

public record TeacherRequest(
        @NotNull(message = "Branch cannot be null")
        Branch branch,
        @NotNull(message = "School ID is required")
        Long schoolId

) {
}
