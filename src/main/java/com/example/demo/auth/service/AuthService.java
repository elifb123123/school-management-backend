package com.example.demo.auth.service;

import com.example.demo.auth.dto.LoginRequest;
import com.example.demo.auth.dto.RefreshRequest;
import com.example.demo.auth.dto.TokenResponse;

public interface AuthService {
    TokenResponse login(LoginRequest loginRequest);

    TokenResponse refresh(RefreshRequest refreshRequest);

    void logout(RefreshRequest refreshRequest);
}
