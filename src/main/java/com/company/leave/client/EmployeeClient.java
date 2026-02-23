package com.company.leave.client;

import java.util.List;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.company.leave.exception.ExternalServiceException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class EmployeeClient {
	
	 private final HttpServletRequest request;

	    private final WebClient webClient =
	            WebClient.builder()
	                    .baseUrl("http://employee-service:8083")
	                    .build();

	    
	    public void validateEmployee(Long employeeId) {
	        String authHeader = request.getHeader("Authorization");

	        webClient.get()
	                .uri("/api/employees/{id}", employeeId)
	                .header("Authorization", authHeader)
	                .retrieve()
	                .onStatus(HttpStatusCode::isError,
	                        res -> Mono.error(new ExternalServiceException("Employee not found")))
	                .toBodilessEntity()
	                .block();
	    }
}
