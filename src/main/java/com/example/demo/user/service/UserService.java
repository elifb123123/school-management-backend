package com.example.demo.user.service;

import com.example.demo.user.dto.MeResponse;
import com.example.demo.user.dto.registration.PrincipalRegistrationRequest;
import com.example.demo.user.dto.registration.StudentRegistrationRequest;
import com.example.demo.user.dto.registration.TeacherRegistrationRequest;
import com.example.demo.user.dto.UserResponse;
import org.springframework.security.core.Authentication;

public interface UserService {

    UserResponse registerPrincipal(PrincipalRegistrationRequest principalRegistrationRequest);

    UserResponse registerTeacher(TeacherRegistrationRequest teacherRegistrationRequest);

    UserResponse registerStudent(StudentRegistrationRequest studentRegistrationRequest);

    MeResponse getCurrentUser(Authentication authentication);
}
