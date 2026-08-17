package com.example.demo.teacher.dto;

import com.example.demo.teacher.persistence.Branch;

public record TeacherResponse(
        Long id,
        String name,
        String email,
        Branch branch,
        String schoolName
) {
}