package com.company.leave.config;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.PrintWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private PrintWriter writer;

    private JwtFilter jwtFilter;

    @BeforeEach
    void setUp() {
        jwtFilter = new JwtFilter(jwtUtil);
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_WithValidToken_SetsAuthentication() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        
        Claims claims = new DefaultClaims();
        claims.setSubject("test@example.com");
        claims.put("role", "USER");
        
        when(jwtUtil.validate("validToken")).thenReturn(claims);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithoutToken_ContinuesChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).validate(any());
    }

    @Test
    void doFilterInternal_WithInvalidToken_ReturnsUnauthorized() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidToken");
        when(jwtUtil.validate("invalidToken")).thenThrow(new RuntimeException("Invalid token"));
        when(response.getWriter()).thenReturn(writer);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(any(), any());
    }
}
