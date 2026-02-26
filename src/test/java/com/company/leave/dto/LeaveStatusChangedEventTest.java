package com.company.leave.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.company.leave.entity.LeaveStatus;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class LeaveStatusChangedEventTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void builder_WithAllFields_CreatesEvent() {
        LeaveStatusChangedEvent event = LeaveStatusChangedEvent.builder()
                .requestId(1L)
                .employeeName(1L)
                .startDate("2024-01-01")
                .endDate("2024-01-05")
                .status(LeaveStatus.APPROVED)
                .build();

        assertThat(event.getRequestId()).isEqualTo(1L);
        assertThat(event.getEmployeeName()).isEqualTo(1L);
        assertThat(event.getStartDate()).isEqualTo("2024-01-01");
        assertThat(event.getEndDate()).isEqualTo("2024-01-05");
        assertThat(event.getStatus()).isEqualTo(LeaveStatus.APPROVED);
    }

    @Test
    void validation_WithValidData_Passes() {
        LeaveStatusChangedEvent event = LeaveStatusChangedEvent.builder()
                .requestId(1L)
                .employeeName(1L)
                .startDate("2024-01-01")
                .endDate("2024-01-05")
                .status(LeaveStatus.PENDING)
                .build();

        Set<ConstraintViolation<LeaveStatusChangedEvent>> violations = validator.validate(event);

        assertThat(violations).isEmpty();
    }

    @Test
    void validation_WithNullRequestId_Fails() {
        LeaveStatusChangedEvent event = LeaveStatusChangedEvent.builder()
                .employeeName(1L)
                .startDate("2024-01-01")
                .endDate("2024-01-05")
                .status(LeaveStatus.PENDING)
                .build();

        Set<ConstraintViolation<LeaveStatusChangedEvent>> violations = validator.validate(event);

        assertThat(violations).isNotEmpty();
    }
}
