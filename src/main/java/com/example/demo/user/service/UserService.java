package com.example.demo.user.service;

import com.example.demo.user.dto.TeacherRegistrationRequest;
import com.example.demo.user.dto.UserRequest;
import com.example.demo.user.dto.UserResponse;

public interface UserService {

    UserResponse registerPrincipal(UserRequest userRequest);

    UserResponse registerTeacher(TeacherRegistrationRequest teacherRegistrationRequest);

//    UserResponse registerStudent(UserRequest userRequest);
}
