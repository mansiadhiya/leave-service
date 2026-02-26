package com.company.leave.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.company.leave.dto.ApiResponse;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void notFound_ReturnsNotFoundResponse() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Not found");
        
        ResponseEntity<ApiResponse<Object>> response = handler.notFound(ex);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void badRequest_ReturnsBadRequestResponse() {
        BadRequestException ex = new BadRequestException("Bad request");
        
        ResponseEntity<ApiResponse<Object>> response = handler.badRequest(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void generic_ReturnsInternalServerError() {
        Exception ex = new Exception("Error");
        
        ResponseEntity<ApiResponse<Object>> response = handler.generic(ex);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
    }
}
