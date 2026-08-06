package com.example.demo.teacher.dto;

public record TeacherResponse(
        Long id,
        String name,
        String email,
        String schoolName
) {
}