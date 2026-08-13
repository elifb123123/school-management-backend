package com.example.demo.user.dto;

import com.example.demo.student.dto.StudentRequest;

public record StudentRegistrationRequest(
        UserRequest userRequest,
        StudentRequest studentRequest
) {
}
