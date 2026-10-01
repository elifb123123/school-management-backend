package com.example.demo.auth.service;

import com.example.demo.auth.dto.LoginRequest;
import com.example.demo.auth.dto.RefreshRequest;
import com.example.demo.auth.dto.TokenResponse;
import com.example.demo.auth.persistence.RefreshToken;
import com.example.demo.auth.persistence.RefreshTokenRepository;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.security.JwtService;
import com.example.demo.user.persistence.User;
import com.example.demo.user.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;


    @Override
    public TokenResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
        //başarısız olursa BadCredentialsException atar.
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", loginRequest.email()));
        String accessToken = jwtService.generateAccessToken(user);
        String refreshTokenRandom = UUID.randomUUID().toString(); // rastgele refresh token için string oluşturur.
        Instant expiryDate = Instant.now().plus(Duration.ofDays(1));
        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenRandom)
                .user(user)
                .expiryDate(expiryDate)
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);
        return new TokenResponse(accessToken, refreshToken.getToken());
    }

    @Override
    public TokenResponse refresh(RefreshRequest refreshRequest) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshRequest.refreshToken())
                .filter(token -> !token.isRevoked() && token.getExpiryDate().isAfter(Instant.now()))
                .orElseThrow(() -> new BadCredentialsException("Geçersiz veya süresi dolmuş refresh token"));
        String newAccessToken = jwtService.generateAccessToken(refreshToken.getUser());
        //Rotation (refresh token güncelleme)
        refreshToken.setRevoked(true);
        String refreshTokenRandom = UUID.randomUUID().toString(); // rastgele refresh token için string oluşturur.
        Instant expiryDate = Instant.now().plus(Duration.ofDays(1));
        RefreshToken newRefreshToken = RefreshToken.builder()
                .token(refreshTokenRandom)
                .user(refreshToken.getUser())
                .expiryDate(expiryDate)
                .revoked(false)
                .build();
        refreshTokenRepository.save(newRefreshToken);

        return new TokenResponse(newAccessToken, newRefreshToken.getToken());
    }

    @Override
    public void logout(RefreshRequest refreshRequest) {
        refreshTokenRepository.findByToken(refreshRequest.refreshToken())
                .filter(token -> !token.isRevoked())
                .ifPresent(refreshToken -> {
                    refreshToken.setRevoked(true);
                });
    }
}

