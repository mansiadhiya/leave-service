package com.company.leave.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class LeaveRequestTest {

    @Test
    void builder_WithAllFields_CreatesLeaveRequest() {
        LeaveRequest leave = LeaveRequest.builder()
                .id(1L)
                .employeeId(1L)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(5))
                .status(LeaveStatus.PENDING)
                .reason("Vacation")
                .createdAt(LocalDateTime.now())
                .build();

        assertThat(leave.getId()).isEqualTo(1L);
        assertThat(leave.getEmployeeId()).isEqualTo(1L);
        assertThat(leave.getStatus()).isEqualTo(LeaveStatus.PENDING);
    }

    @Test
    void settersAndGetters_WorkCorrectly() {
        LeaveRequest leave = new LeaveRequest();
        leave.setId(2L);
        leave.setEmployeeId(2L);
        leave.setStatus(LeaveStatus.APPROVED);
        leave.setReason("Medical");

        assertThat(leave.getId()).isEqualTo(2L);
        assertThat(leave.getEmployeeId()).isEqualTo(2L);
        assertThat(leave.getStatus()).isEqualTo(LeaveStatus.APPROVED);
        assertThat(leave.getReason()).isEqualTo("Medical");
    }
}
