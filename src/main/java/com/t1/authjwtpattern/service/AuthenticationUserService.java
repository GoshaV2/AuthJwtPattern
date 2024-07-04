package com.t1.authjwtpattern.service;

import com.t1.authjwtpattern.controller.dto.request.AuthenticationRequest;
import com.t1.authjwtpattern.controller.dto.request.UserRegisterRequest;
import com.t1.authjwtpattern.controller.dto.response.AuthenticationResponse;
import com.t1.authjwtpattern.exception.AuthorizationException;
import com.t1.authjwtpattern.exception.NotFoundElementException;
import com.t1.authjwtpattern.exception.UserAlreadyExist;
import com.t1.authjwtpattern.model.entity.User;
import com.t1.authjwtpattern.model.security.Role;
import com.t1.authjwtpattern.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationUserService {
    public static final Role BASE_ROLE = Role.USER;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtService;
    private final TokenService tokenService;
    @Value("${spring.security.token.access.secret}")
    private String accessTokenSecret;
    @Value("${spring.security.token.refresh.secret}")
    private String refreshTokenSecret;

    @Transactional
    public AuthenticationResponse register(UserRegisterRequest request) {
        if (userRepository.existsByLogin(request.getLogin())) {
            throw new UserAlreadyExist("User already exist");
        }
        User user = User.builder()
                .roles(Set.of(BASE_ROLE))
                .login(request.getLogin())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);
        return getAuthenticationResponse(user);
    }

    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        Optional<User> employeeOptional = userRepository.findByLogin(authenticationRequest.getLogin());
        if (employeeOptional.isEmpty()) {
            throw new AuthorizationException("Wrong password or login");
        }
        User user = employeeOptional.get();
        if (!passwordEncoder.matches(authenticationRequest.getPassword(),
                user.getPassword())) {
            throw new AuthorizationException("Wrong password or login");
        }
        return getAuthenticationResponse(user);
    }

    private AuthenticationResponse getAuthenticationResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user.getRoles(),
                user.getId(), accessTokenSecret);
        String refreshToken = generateRefreshToken(user);
        return AuthenticationResponse.builder()
                .roles(user.getRoles())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpired(jwtService.extractExpiration(accessToken, accessTokenSecret))
                .refreshTokenExpired(jwtService.extractExpiration(refreshToken, refreshTokenSecret))
                .build();
    }

    @Transactional
    public AuthenticationResponse refreshTokens(String token) {
        validateRefreshToken(token);
        UUID userId = extractUserIdFromRefreshToken(token, refreshTokenSecret);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundElementException(userId, User.class));
        return getAuthenticationResponse(user);
    }

    @Transactional
    public void logout(String token) {
        validateRefreshToken(token);
        tokenService.deleteToken(extractUserIdFromRefreshToken(token, refreshTokenSecret));
    }

    private void validateRefreshToken(String token) {
        if (!jwtService.isTokenValid(token, refreshTokenSecret)) {
            throw new AuthorizationException("Refresh token expired.");
        }
        if (!tokenService.exist(extractUserIdFromRefreshToken(token, refreshTokenSecret), token)) {
            throw new AuthorizationException("This is revoked refresh token");
        }
    }

    private UUID extractUserIdFromRefreshToken(String token, String refreshTokenSecret) {
        return jwtService.extractId(token, refreshTokenSecret);
    }

    private String generateRefreshToken(User user) {
        String token = jwtService.generateRefreshToken(user.getId(), refreshTokenSecret);
        tokenService.addToken(user.getId(), token);
        return token;
    }
}
