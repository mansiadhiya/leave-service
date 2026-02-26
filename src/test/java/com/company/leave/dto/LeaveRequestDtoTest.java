package com.company.leave.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class LeaveRequestDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void validRequest_PassesValidation() {
        LeaveRequestDto dto = new LeaveRequestDto();
        dto.setEmployeeId(1L);
        dto.setStartDate(LocalDate.now().plusDays(1));
        dto.setEndDate(LocalDate.now().plusDays(5));
        dto.setReason("Vacation");

        Set<ConstraintViolation<LeaveRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void nullEmployeeId_FailsValidation() {
        LeaveRequestDto dto = new LeaveRequestDto();
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(5));

        Set<ConstraintViolation<LeaveRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void nullStartDate_FailsValidation() {
        LeaveRequestDto dto = new LeaveRequestDto();
        dto.setEmployeeId(1L);
        dto.setEndDate(LocalDate.now().plusDays(5));

        Set<ConstraintViolation<LeaveRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }
}
