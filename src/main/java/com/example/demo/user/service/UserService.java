package com.example.demo.user.service;

import com.example.demo.user.dto.UserRequest;
import com.example.demo.user.dto.UserResponse;

public interface UserService {

    UserResponse registerUser(UserRequest userRequest);
}
