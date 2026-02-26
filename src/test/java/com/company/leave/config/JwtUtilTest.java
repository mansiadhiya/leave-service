package com.company.leave.config;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String secret = "mySecretKeyForTestingPurposesOnly1234567890";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", secret);
    }

    @Test
    void validate_WithValidToken_ReturnsClaims() {
        String token = Jwts.builder()
                .setSubject("test@example.com")
                .claim("role", "USER")
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();

        Claims claims = jwtUtil.validate(token);

        assertThat(claims.getSubject()).isEqualTo("test@example.com");
        assertThat(claims.get("role")).isEqualTo("USER");
    }

    @Test
    void validate_WithInvalidToken_ThrowsException() {
        assertThatThrownBy(() -> jwtUtil.validate("invalid.token.here"))
                .isInstanceOf(Exception.class);
    }
}
