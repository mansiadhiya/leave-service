package com.company.leave.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ExceptionTest {

    @Test
    void badRequestException_CreatesWithMessage() {
        BadRequestException ex = new BadRequestException("Bad request");
        assertEquals("Bad request", ex.getMessage());
    }

    @Test
    void resourceNotFoundException_CreatesWithMessage() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Not found");
        assertEquals("Not found", ex.getMessage());
    }

    @Test
    void externalServiceException_CreatesWithMessage() {
        ExternalServiceException ex = new ExternalServiceException("Service error");
        assertEquals("Service error", ex.getMessage());
    }
}
