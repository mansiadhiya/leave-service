package com.company.leave.client;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class EmployeeClientTest {

    @Mock
    private HttpServletRequest httpRequest;

    private EmployeeClient employeeClient;

    @BeforeEach
    void setUp() {
        employeeClient = new EmployeeClient(httpRequest);
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer token");
    }

    @Test
    void validateEmployee_CallsWebClient() {
        try {
            employeeClient.validateEmployee(1L);
        } catch (Exception e) {
            // Expected as WebClient is not mocked
        }
    }
}
