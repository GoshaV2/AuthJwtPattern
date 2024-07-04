package com.t1.authjwtpattern.service;

import com.t1.authjwtpattern.model.entity.RefreshToken;
import com.t1.authjwtpattern.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public void addToken(UUID userId, String token) {
        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findById(userId);
        RefreshToken refreshToken;
        refreshToken = refreshTokenOptional.orElseGet(() ->
                RefreshToken.builder()
                        .userId(userId)
                        .build());
        refreshToken.setToken(token);
        refreshTokenRepository.save(refreshToken);
    }

    public void deleteToken(UUID userId) {
        refreshTokenRepository.deleteById(userId);
    }

    public boolean exist(UUID userId, String token) {
        return refreshTokenRepository.existsByUserIdAndToken(userId, token);
    }
}
