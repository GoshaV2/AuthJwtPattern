package com.t1.authjwtpattern.controller.dto.response;

import com.t1.authjwtpattern.model.security.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String accessToken;
    private String refreshToken;
    private Date accessTokenExpired;
    private Date refreshTokenExpired;
    private Set<Role> roles;
}
