package com.company.leave.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.company.leave.dto.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	
	 @ExceptionHandler(ResourceNotFoundException.class)
	    public ResponseEntity<ApiResponse<?>> notFound(ResourceNotFoundException ex) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body(ApiResponse.error(ex.getMessage()));
	    }

	    @ExceptionHandler(BadRequestException.class)
	    public ResponseEntity<ApiResponse<?>> badRequest(BadRequestException ex) {
	        return ResponseEntity.badRequest()
	                .body(ApiResponse.error(ex.getMessage()));
	    }

//	    @ExceptionHandler(ExternalServiceException.class)
//	    public ResponseEntity<ApiResponse<?>> external(ExternalServiceException ex) {
//	        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
//	                .body(ApiResponse.error(ex.getMessage()));
//	    }

	    @ExceptionHandler(Exception.class)
	    public ResponseEntity<ApiResponse<?>> generic(Exception ex) {
	        log.error("Unhandled exception", ex);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(ApiResponse.error("Internal server error"));
	    }

}
