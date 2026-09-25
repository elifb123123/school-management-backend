package com.example.demo.user.dto.registration;

import com.example.demo.student.dto.StudentRequest;
import com.example.demo.user.dto.UserRequest;

public record StudentRegistrationRequest(
        UserRequest userRequest,
        StudentRequest studentRequest
) {
}
