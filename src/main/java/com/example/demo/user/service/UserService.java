package com.example.demo.user.service;

import com.example.demo.user.dto.PrincipalRegistrationRequest;
import com.example.demo.user.dto.StudentRegistrationRequest;
import com.example.demo.user.dto.TeacherRegistrationRequest;
import com.example.demo.user.dto.UserResponse;

public interface UserService {

    UserResponse registerPrincipal(PrincipalRegistrationRequest principalRegistrationRequest);

    UserResponse registerTeacher(TeacherRegistrationRequest teacherRegistrationRequest, String principalEmail);

    UserResponse registerStudent(StudentRegistrationRequest studentRegistrationRequest, String principalEmail);
}
