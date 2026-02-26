package com.company.leave.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.company.leave.entity.LeaveRequest;
import com.company.leave.entity.LeaveStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class LeaveEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private tools.jackson.databind.ObjectMapper objectMapper;

    private LeaveEventPublisher eventPublisher;
    private LeaveRequest leaveRequest;

    @BeforeEach
    void setUp() {
        eventPublisher = new LeaveEventPublisher(rabbitTemplate, objectMapper);
        
        leaveRequest = LeaveRequest.builder()
                .id(1L)
                .employeeId(1L)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(5))
                .status(LeaveStatus.PENDING)
                .reason("Vacation")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void publishLeaveEvent_WithValidLeave_PublishesEvent() throws Exception {
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"leaveId\":1}");

        eventPublisher.publishLeaveEvent(leaveRequest);

        verify(rabbitTemplate).convertAndSend(eq("notification.exchange"), eq("leave.status.changed"), anyString());
    }

    @Test
    void publishLeaveEvent_WhenSerializationFails_ThrowsException() throws Exception {
        when(objectMapper.writeValueAsString(any())).thenThrow(new RuntimeException("Serialization failed"));

        assertThrows(RuntimeException.class, () -> eventPublisher.publishLeaveEvent(leaveRequest));
    }
}
