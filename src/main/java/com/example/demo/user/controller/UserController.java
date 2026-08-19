package com.example.demo.user.controller;

import com.example.demo.user.dto.MeResponse;
import com.example.demo.user.dto.PrincipalRegistrationRequest;
import com.example.demo.user.dto.StudentRegistrationRequest;
import com.example.demo.user.dto.TeacherRegistrationRequest;
import com.example.demo.user.dto.UserResponse;
import com.example.demo.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")

public class UserController {

    private final UserService userService;


    UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register/principal")
    public UserResponse registerPrincipal(@RequestBody PrincipalRegistrationRequest principalRegistrationRequest) {
        return userService.registerPrincipal(principalRegistrationRequest);
    }

    @PostMapping("/register/teacher")
    public UserResponse registerTeacher(@RequestBody TeacherRegistrationRequest teacherRegistrationRequest) {
        return userService.registerTeacher(teacherRegistrationRequest);
    }

    @PostMapping("/register/student")
    public UserResponse registerStudent(@RequestBody StudentRegistrationRequest studentRegistrationRequest) {
        return userService.registerStudent(studentRegistrationRequest);
    }

    @GetMapping("/me")
    public MeResponse getCurrentUser(Authentication authentication) {
        return userService.getCurrentUser(authentication);
    }
}
