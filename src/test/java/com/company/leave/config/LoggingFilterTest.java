package com.company.leave.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class LoggingFilterTest {

    private final LoggingFilter filter = new LoggingFilter();

    @Test
    void doFilter_AddsTraceIdFromHeader() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        
        when(request.getHeader("X-Trace-Id")).thenReturn("trace-123");
        when(request.getHeader("X-Correlation-Id")).thenReturn("corr-456");
        
        filter.doFilter(request, response, chain);
        
        verify(response).setHeader(eq("X-Trace-Id"), eq("trace-123"));
        verify(chain).doFilter(request, response);
    }

    @Test
    void doFilter_GeneratesTraceIdWhenMissing() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        
        when(request.getHeader("X-Trace-Id")).thenReturn(null);
        when(request.getHeader("X-Correlation-Id")).thenReturn(null);
        
        filter.doFilter(request, response, chain);
        
        verify(response).setHeader(eq("X-Trace-Id"), anyString());
        verify(response).setHeader(eq("X-Correlation-Id"), anyString());
        verify(response).setHeader(eq("X-Request-Id"), anyString());
        verify(chain).doFilter(request, response);
    }

    @Test
    void doFilter_ClearsMDCAfterProcessing() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        
        when(request.getHeader("X-Trace-Id")).thenReturn("trace-123");
        when(request.getHeader("X-Correlation-Id")).thenReturn("corr-456");
        
        filter.doFilter(request, response, chain);
        
        assertNull(MDC.get("traceId"));
        assertNull(MDC.get("correlationId"));
    }
}
