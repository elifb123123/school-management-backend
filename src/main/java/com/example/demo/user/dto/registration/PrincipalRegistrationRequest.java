package com.example.demo.user.dto.registration;

import com.example.demo.school.dto.SchoolRequest;
import com.example.demo.user.dto.UserRequest;

public record PrincipalRegistrationRequest(
        UserRequest userRequest,
        SchoolRequest schoolRequest
) {
}
