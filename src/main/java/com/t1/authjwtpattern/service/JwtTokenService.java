package com.t1.authjwtpattern.service;

import com.t1.authjwtpattern.model.security.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtTokenService {
    @Value("${spring.security.token.access.expiration}")
    private long accessExpiration;
    @Value("${spring.security.token.refresh.expiration}")
    private long refreshExpiration;

    public String generateAccessToken(Set<Role> roles, UUID id, String secretKey) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        claims.put("id", id);
        return Jwts
                .builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessExpiration * 1000))
                .signWith(getSignInKey(secretKey), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(UUID userId, String secretKey) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration * 1000))
                .signWith(getSignInKey(secretKey), SignatureAlgorithm.HS256)
                .compact();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, String secretKey) {
        final Claims claims = extractAllClaims(token, secretKey);
        return claimsResolver.apply(claims);
    }

    private boolean isTokenExpired(String token, String secretKey) {
        return extractExpiration(token, secretKey).before(new Date());
    }

    public Date extractExpiration(String token, String secretKey) {
        return extractClaim(token, Claims::getExpiration, secretKey);
    }

    public Date extractIssuedAt(String token, String secretKey) {
        return extractClaim(token, Claims::getIssuedAt, secretKey);
    }

    private Claims extractAllClaims(String token, String secretKey) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token, String secretKey) {
        return extractClaim(token, Claims::getSubject, secretKey);
    }

    public boolean isTokenValid(String token, String secretKey) {
        return !isTokenExpired(token, secretKey);
    }

    public UUID extractId(String token, String secretKey) {
        return UUID.fromString(extractClaim(token, claims -> claims.get("id").toString(), secretKey));
    }

    public Set<String> extractRoles(String token, String secretKey) {
        return new HashSet<>(((List<String>) extractClaim(token, claims -> claims.get("roles"), secretKey)));
    }

    public String extractUserType(String token, String secretKey) {
        String type = (String) extractClaim(token, claims -> claims.get("userType"), secretKey);
        return type;
    }
}
