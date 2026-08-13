package com.example.demo.user.controller;

import com.example.demo.user.dto.TeacherRegistrationRequest;
import com.example.demo.user.dto.UserRequest;
import com.example.demo.user.dto.UserResponse;
import com.example.demo.user.service.UserService;
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
    public UserResponse registerPrincipal(@RequestBody UserRequest userRequest) {
        return userService.registerPrincipal(userRequest);
    }

    @PostMapping("/register/teacher")
    public UserResponse registerTeacher(@RequestBody TeacherRegistrationRequest teacherRegistrationRequest) {
        return userService.registerTeacher(teacherRegistrationRequest);
    }

//    @PostMapping("/register/student")
//    public UserResponse registerStudent(@RequestBody UserRequest userRequest) {
//        return userService.registerStudent(userRequest);
//    }
}
