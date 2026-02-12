package com.microservices.ems.auth_service.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.ems.auth_service.entity.User;
import com.microservices.ems.auth_service.repository.UserRepository;
import com.microservices.ems.auth_service.security.JwtUtil;

import io.jsonwebtoken.Claims;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;

    public AuthController(UserRepository repo,
                          PasswordEncoder encoder,
                          JwtUtil jwt) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        repo.save(user);
        return "User registered successfully";
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody User request) {

    User user = repo.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

    if (!encoder.matches(request.getPassword(), user.getPassword())) {
        throw new RuntimeException("Invalid password");
    }

    String accessToken = JwtUtil.generateAccessToken(user.getUsername(), user.getRole());
    String refreshToken = JwtUtil.generateRefreshToken(user.getUsername());

    Map<String, String> tokens = new HashMap<>();
    tokens.put("accessToken", accessToken);
    tokens.put("refreshToken", refreshToken);

    return tokens;
}

@PostMapping("/refresh")
public Map<String, String> refresh(@RequestBody Map<String, String> request) {

    String refreshToken = request.get("refreshToken");

    Claims claims = JwtUtil.getClaims(refreshToken);

    if (!"refresh".equals(claims.get("type"))) {
        throw new RuntimeException("Invalid refresh token");
    }

    String username = claims.getSubject();

    User user = repo.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

    String newAccessToken =
            JwtUtil.generateAccessToken(user.getUsername(), user.getRole());

    Map<String, String> response = new HashMap<>();
    response.put("accessToken", newAccessToken);

    return response;
}


}
