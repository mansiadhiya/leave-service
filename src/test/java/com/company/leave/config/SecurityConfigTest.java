package com.company.leave.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

class SecurityConfigTest {

    @Test
    void filter_CreatesSecurityFilterChain() throws Exception {
        JwtFilter jwtFilter = mock(JwtFilter.class);
        SecurityConfig config = new SecurityConfig(jwtFilter);
        HttpSecurity http = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);
        
        when(http.csrf(any())).thenReturn(http);
        when(http.sessionManagement(any())).thenReturn(http);
        when(http.authorizeHttpRequests(any())).thenReturn(http);
        when(http.addFilterBefore(any(), any())).thenReturn(http);
        //when(http.build()).thenReturn(mock(SecurityFilterChain.class));
        
        SecurityFilterChain result = config.filter(http);
        
        assertNotNull(result);
        verify(http).build();
    }
}
