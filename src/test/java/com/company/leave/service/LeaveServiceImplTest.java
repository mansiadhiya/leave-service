package com.company.leave.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.company.leave.client.EmployeeClient;
import com.company.leave.dto.LeaveRequestDto;
import com.company.leave.entity.LeaveRequest;
import com.company.leave.entity.LeaveStatus;
import com.company.leave.exception.BadRequestException;
import com.company.leave.exception.ExternalServiceException;
import com.company.leave.exception.ResourceNotFoundException;
import com.company.leave.repository.LeaveRepository;

@ExtendWith(MockitoExtension.class)
public class LeaveServiceImplTest {

    @Mock
    private LeaveRepository repository;

    @Mock
    private EmployeeClient employeeClient;

    @InjectMocks
    private LeaveServiceImpl service;

    private LeaveRequest existingLeave;

    @BeforeEach
    void init() {
        existingLeave = LeaveRequest.builder()
                .id(1L)
                .employeeId(101L)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(2))
                .status(LeaveStatus.PENDING)
                .reason("Vacation")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Create Leave Tests")
    class CreateLeave {

        @Test
        @DisplayName("Should create leave when input is valid")
        void shouldCreateLeaveSuccessfully() {
            LeaveRequestDto dto = validDto();
            when(repository.save(any())).thenReturn(existingLeave);

            LeaveRequest result = service.createLeave(dto);

            assertThat(result)
                    .isNotNull()
                    .extracting(LeaveRequest::getEmployeeId)
                    .isEqualTo(101L);
            verify(employeeClient).validateEmployee(101L);
            verify(repository).save(any());
        }

        @Test
        @DisplayName("Should throw exception when end date before start date")
        void shouldRejectInvalidDateRange() {
            LeaveRequestDto dto = validDto();
            dto.setEndDate(dto.getStartDate().minusDays(1));

            assertThatThrownBy(() -> service.createLeave(dto))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("End date");
            verifyNoInteractions(repository);
        }

        @Test
        @DisplayName("Should throw exception when employee does not exist")
        void shouldFailIfEmployeeMissing() {
            LeaveRequestDto dto = validDto();
            doThrow(new ExternalServiceException("Employee not found"))
                    .when(employeeClient)
                    .validateEmployee(101L);

            assertThatThrownBy(() -> service.createLeave(dto))
                    .isInstanceOf(ExternalServiceException.class);
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Fetch Leave Tests")
    class FetchLeaves {

        @Test
        @DisplayName("Should return leave list for employee")
        void shouldReturnLeaves() {
            doNothing().when(employeeClient).validateEmployee(101L);
            when(repository.findByEmployeeId(101L))
                    .thenReturn(List.of(existingLeave));

            List<LeaveRequest> result = service.getByEmployee(101L);

            assertThat(result)
                    .hasSize(1)
                    .first()
                    .extracting(LeaveRequest::getStatus)
                    .isEqualTo(LeaveStatus.PENDING);
            verify(employeeClient).validateEmployee(101L);
        }

        @Test
        @DisplayName("Should throw exception if no leaves exist")
        void shouldThrowWhenNoLeavesFound() {
            doNothing().when(employeeClient).validateEmployee(101L);
            when(repository.findByEmployeeId(101L))
                    .thenReturn(Collections.emptyList());

            assertThatThrownBy(() -> service.getByEmployee(101L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Employee not found or no leave records");
            verify(repository).findByEmployeeId(101L);
        }
    }

    @Nested
    @DisplayName("Update Status Tests")
    class UpdateStatus {

        @Test
        @DisplayName("Should update leave status")
        void shouldUpdateStatus() {
            when(repository.findById(1L))
                    .thenReturn(Optional.of(existingLeave));
            when(repository.save(any()))
                    .thenReturn(existingLeave);

            LeaveRequest updated = service.updateStatus(1L, LeaveStatus.APPROVED);

            assertThat(updated.getStatus()).isEqualTo(LeaveStatus.APPROVED);
        }

        @Test
        @DisplayName("Should throw exception when leave not found")
        void shouldThrowWhenLeaveMissing() {
            when(repository.findById(1L))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.updateStatus(1L, LeaveStatus.APPROVED))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    private LeaveRequestDto validDto() {
        LeaveRequestDto dto = new LeaveRequestDto();
        dto.setEmployeeId(101L);
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(2));
        dto.setReason("Travel");
        return dto;
    }
}
