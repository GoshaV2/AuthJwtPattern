package com.t1.authjwtpattern.filter;

import com.t1.authjwtpattern.exception.AuthorizationException;
import com.t1.authjwtpattern.model.security.CustomPrincipal;
import com.t1.authjwtpattern.model.security.Role;
import com.t1.authjwtpattern.service.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {
    private static final int BEARER_LENGTH = 7;
    private final JwtTokenService jwtService;
    @Value("${spring.security.token.access.secret}")
    private String tokenSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final UUID userId;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.intern().substring(BEARER_LENGTH);
        if (!jwtService.isTokenValid(jwt, tokenSecret)) {
            throw new AuthorizationException("Token is invalid");
        }
        userId = jwtService.extractId(jwt, tokenSecret);
        Set<Role> roles = jwtService.extractRoles(jwt, tokenSecret).stream()
                .map(Role::valueOf)
                .collect(Collectors.toSet());
        Collection<GrantedAuthority> grantedAuthorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name())).collect(Collectors.toSet());
        CustomPrincipal customPrincipal = new CustomPrincipal(roles, userId);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                customPrincipal,
                null,
                grantedAuthorities
        );
        authToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }
}
