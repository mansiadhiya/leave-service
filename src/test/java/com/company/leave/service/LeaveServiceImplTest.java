package com.company.leave.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.leave.client.EmployeeClient;
import com.company.leave.dto.LeaveRequestDto;
import com.company.leave.entity.LeaveRequest;
import com.company.leave.entity.LeaveStatus;
import com.company.leave.exception.BadRequestException;
import com.company.leave.exception.ResourceNotFoundException;
import com.company.leave.mapper.LeaveMapper;
import com.company.leave.repository.LeaveRepository;

@ExtendWith(MockitoExtension.class)
class LeaveServiceImplTest {

    @Mock
    private LeaveRepository leaveRepository;

    @Mock
    private EmployeeClient employeeClient;

    @Mock
    private LeaveEventPublisher eventPublisher;

    @Mock
    private LeaveMapper leaveMapper;

    @InjectMocks
    private LeaveServiceImpl leaveService;

    private LeaveRequestDto leaveRequestDto;
    private LeaveRequest leaveRequest;

    @BeforeEach
    void setUp() {
        leaveRequestDto = new LeaveRequestDto();
        leaveRequestDto.setEmployeeId(1L);
        leaveRequestDto.setStartDate(LocalDate.now().plusDays(1));
        leaveRequestDto.setEndDate(LocalDate.now().plusDays(5));
        leaveRequestDto.setReason("Vacation");

        leaveRequest = LeaveRequest.builder()
                .id(1L)
                .employeeId(1L)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(5))
                .status(LeaveStatus.PENDING)
                .reason("Vacation")
                .build();
    }

    @Test
    void createLeave_WithValidRequest_CreatesLeave() {
       // when(employeeClient.validateEmployee(1L)).thenReturn(true);
        when(leaveMapper.toEntity(leaveRequestDto)).thenReturn(leaveRequest);
        when(leaveRepository.save(any(LeaveRequest.class))).thenReturn(leaveRequest);

        LeaveRequest result = leaveService.createLeave(leaveRequestDto);

        assertNotNull(result);
        assertEquals(LeaveStatus.PENDING, result.getStatus());
        verify(leaveRepository).save(any(LeaveRequest.class));
        verify(eventPublisher).publishLeaveEvent(any(LeaveRequest.class));
    }

    @Test
    void createLeave_WithInvalidDates_ThrowsException() {
        leaveRequestDto.setEndDate(LocalDate.now().minusDays(1));

        assertThrows(BadRequestException.class, () -> leaveService.createLeave(leaveRequestDto));
        verify(leaveRepository, never()).save(any());
    }

    @Test
    void createLeave_WhenEventPublishFails_StillCreatesLeave() {
       // when(employeeClient.validateEmployee(1L)).thenReturn(true);
        when(leaveMapper.toEntity(leaveRequestDto)).thenReturn(leaveRequest);
        when(leaveRepository.save(any(LeaveRequest.class))).thenReturn(leaveRequest);
        doThrow(new RuntimeException("Event failed")).when(eventPublisher).publishLeaveEvent(any());

        LeaveRequest result = leaveService.createLeave(leaveRequestDto);

        assertNotNull(result);
        verify(leaveRepository).save(any(LeaveRequest.class));
    }

    @Test
    void updateStatus_WithValidId_UpdatesStatus() {
        when(leaveRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));
        when(leaveRepository.save(any(LeaveRequest.class))).thenReturn(leaveRequest);

        LeaveRequest result = leaveService.updateStatus(1L, LeaveStatus.APPROVED);

        assertNotNull(result);
        verify(leaveRepository).save(any(LeaveRequest.class));
        verify(eventPublisher).publishLeaveEvent(any(LeaveRequest.class));
    }

    @Test
    void updateStatus_WithInvalidId_ThrowsException() {
        when(leaveRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> leaveService.updateStatus(999L, LeaveStatus.APPROVED));
        verify(leaveRepository, never()).save(any());
    }

    @Test
    void updateStatus_WhenEventPublishFails_StillUpdates() {
        when(leaveRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));
        when(leaveRepository.save(any(LeaveRequest.class))).thenReturn(leaveRequest);
        doThrow(new RuntimeException("Event failed")).when(eventPublisher).publishLeaveEvent(any());

        LeaveRequest result = leaveService.updateStatus(1L, LeaveStatus.APPROVED);

        assertNotNull(result);
        verify(leaveRepository).save(any(LeaveRequest.class));
    }

    @Test
    void getByEmployee_WithValidEmployee_ReturnsLeaves() {
       // when(employeeClient.validateEmployee(1L)).thenReturn(true);
        when(leaveRepository.findByEmployeeId(1L)).thenReturn(List.of(leaveRequest));

        List<LeaveRequest> result = leaveService.getByEmployee(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(leaveRepository).findByEmployeeId(1L);
    }

    @Test
    void getByEmployee_WithNoLeaves_ThrowsException() {
      //  when(employeeClient.validateEmployee(1L)).thenReturn(true);
        when(leaveRepository.findByEmployeeId(1L)).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> leaveService.getByEmployee(1L));
    }

    @Test
    void getByEmployee_WithNullList_ThrowsException() {
       // when(employeeClient.validateEmployee(1L)).thenReturn(true);
        when(leaveRepository.findByEmployeeId(1L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> leaveService.getByEmployee(1L));
    }
}
