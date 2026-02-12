package com.microservices.ems.auth_service.security;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;   


@Component
public class JwtUtil {

    private static final String SECRET =
            "ems-secret-key-ems-secret-key-ems";

    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    // 15 minutes
    private static final long ACCESS_EXPIRATION = 1000 * 60 * 15;

    // 7 days
    private static final long REFRESH_EXPIRATION = 1000L * 60 * 60 * 24 * 7;

    // Access Token
    public static String generateAccessToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION))
                .signWith(KEY)
                .compact();
    }

    // Refresh Token
    public static String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .claim("type", "refresh")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION))
                .signWith(KEY)
                .compact();
    }

    public static Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
