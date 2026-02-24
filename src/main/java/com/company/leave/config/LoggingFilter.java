package com.company.leave.config;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(1)
public class LoggingFilter implements Filter {

    private static final String TRACE_ID = "traceId";
    private static final String CORRELATION_ID = "correlationId";
    private static final String REQUEST_ID = "requestId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            String traceId = httpRequest.getHeader("X-Trace-Id");
            if (traceId == null || traceId.isEmpty()) {
                traceId = UUID.randomUUID().toString();
            }

            String correlationId = httpRequest.getHeader("X-Correlation-Id");
            if (correlationId == null || correlationId.isEmpty()) {
                correlationId = UUID.randomUUID().toString();
            }

            String requestId = UUID.randomUUID().toString();

            MDC.put(TRACE_ID, traceId);
            MDC.put(CORRELATION_ID, correlationId);
            MDC.put(REQUEST_ID, requestId);

            httpResponse.setHeader("X-Trace-Id", traceId);
            httpResponse.setHeader("X-Correlation-Id", correlationId);
            httpResponse.setHeader("X-Request-Id", requestId);

            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
