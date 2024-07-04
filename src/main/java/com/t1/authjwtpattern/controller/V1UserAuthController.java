package com.t1.authjwtpattern.controller;

import com.t1.authjwtpattern.controller.dto.request.AuthenticationRequest;
import com.t1.authjwtpattern.controller.dto.request.RefreshTokenRequest;
import com.t1.authjwtpattern.controller.dto.request.UserRegisterRequest;
import com.t1.authjwtpattern.controller.dto.response.AuthenticationResponse;
import com.t1.authjwtpattern.service.AuthenticationUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/auth")
@RequiredArgsConstructor
public class V1UserAuthController {
    private final AuthenticationUserService authenticationUserService;


    @PostMapping
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationUserService.authenticate(request));
    }

    @PostMapping("/refreshTokens")
    public ResponseEntity<AuthenticationResponse> refreshTokens(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authenticationUserService.refreshTokens(request.getRefreshToken()));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody UserRegisterRequest request) {
        return ResponseEntity.ok(authenticationUserService.register(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logoutClient(@RequestBody RefreshTokenRequest request) {
        authenticationUserService.logout(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }
}
