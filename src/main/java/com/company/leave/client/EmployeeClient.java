package com.company.leave.client;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.company.leave.exception.ExternalServiceException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmployeeClient {
	
	 private final HttpServletRequest httpRequest;

	    private final WebClient employeeWebClient =
	            WebClient.builder()
	                    .baseUrl("http://employee-service:8083")
	                    .build();

	    @CircuitBreaker(name = "employeeService", fallbackMethod = "validateEmployeeFallback")
	    @Retry(name = "employeeService")
	    public void validateEmployee(Long employeeId) {
	        log.info("Validating employee with employeeId={}", employeeId);
	        String authorizationHeader = httpRequest.getHeader("Authorization");

	        try {
	            employeeWebClient.get()
	                    .uri("/api/employees/{id}", employeeId)
	                    .header("Authorization", authorizationHeader)
	                    .retrieve()
	                    .onStatus(HttpStatusCode::isError,
	                            errorResponse -> Mono.error(new ExternalServiceException("Employee not found with id: " + employeeId)))
	                    .toBodilessEntity()
	                    .block();
	            log.info("Employee validation successful for employeeId={}", employeeId);
	        } catch (Exception validationException) {
	            log.info("Failed to validate employee with employeeId={}", employeeId, validationException);
	            throw validationException;
	        }
	    }
	    
	    private void validateEmployeeFallback(Long employeeId, Exception fallbackException) {
	        log.info("Circuit breaker fallback triggered for employeeId={}, reason={}", employeeId, fallbackException.getMessage());
	        throw new ExternalServiceException("Employee service is currently unavailable. Please try again later.");
	    }
}
