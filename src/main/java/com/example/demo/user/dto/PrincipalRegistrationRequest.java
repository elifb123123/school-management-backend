package com.example.demo.user.dto;

import com.example.demo.school.dto.SchoolRequest;

public record PrincipalRegistrationRequest(
        UserRequest userRequest,
        SchoolRequest schoolRequest
) {
}
