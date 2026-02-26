package com.company.leave.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.company.leave.client.EmployeeClient;
import com.company.leave.dto.LeaveRequestDto;
import com.company.leave.entity.*;
import com.company.leave.exception.BadRequestException;
import com.company.leave.exception.ResourceNotFoundException;
import com.company.leave.mapper.LeaveMapper;
import com.company.leave.repository.LeaveRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LeaveServiceImpl implements LeaveService {

	private final LeaveRepository leaveRepository;
	private final EmployeeClient employeeClient;
	private final LeaveEventPublisher eventPublisher;
	private final LeaveMapper leaveMapper;

	@Override
	public LeaveRequest createLeave(LeaveRequestDto leaveRequestDto) {
		log.info("Creating leave request for employeeId={}, startDate={}, endDate={}", 
				leaveRequestDto.getEmployeeId(), leaveRequestDto.getStartDate(), leaveRequestDto.getEndDate());

		validateDates(leaveRequestDto);

		employeeClient.validateEmployee(leaveRequestDto.getEmployeeId());

		LeaveRequest newLeaveRequest = leaveMapper.toEntity(leaveRequestDto);
		newLeaveRequest.setStatus(LeaveStatus.PENDING);
		newLeaveRequest.setCreatedAt(LocalDateTime.now());

		LeaveRequest savedLeaveRequest = leaveRepository.save(newLeaveRequest);
		log.info("Leave request created successfully with leaveId={}, employeeId={}, status={}", 
				savedLeaveRequest.getId(), savedLeaveRequest.getEmployeeId(), savedLeaveRequest.getStatus());

		try {
			eventPublisher.publishLeaveEvent(savedLeaveRequest);
			log.info("Leave event published successfully for leaveId={}", savedLeaveRequest.getId());
		} catch (Exception publishException) {
			log.info("Failed to publish leave create event for leaveId={}, employeeId={}", 
					savedLeaveRequest.getId(), savedLeaveRequest.getEmployeeId(), publishException);
		}

		return savedLeaveRequest;
	}

	private void validateDates(LeaveRequestDto leaveRequestDto) {
		log.debug("Validating dates for startDate={}, endDate={}", leaveRequestDto.getStartDate(), leaveRequestDto.getEndDate());
		if (leaveRequestDto.getEndDate().isBefore(leaveRequestDto.getStartDate())) {
			log.warn("Invalid date range: endDate={} is before startDate={}", leaveRequestDto.getEndDate(), leaveRequestDto.getStartDate());
			throw new BadRequestException("End date must be after start date");
		}
	}

	@Override
	public LeaveRequest updateStatus(Long leaveId, LeaveStatus newStatus) {
		log.info("Updating leave status for leaveId={}, newStatus={}", leaveId, newStatus);

		LeaveRequest existingLeaveRequest = leaveRepository.findById(leaveId)
				.orElseThrow(() -> {
					log.info("Leave not found with leaveId={}", leaveId);
					return new ResourceNotFoundException("Leave not found");
				});

		LeaveStatus previousStatus = existingLeaveRequest.getStatus();
		existingLeaveRequest.setStatus(newStatus);
		LeaveRequest updatedLeaveRequest = leaveRepository.save(existingLeaveRequest);
		log.info("Leave status updated successfully for leaveId={}, employeeId={}, oldStatus={}, newStatus={}", 
				updatedLeaveRequest.getId(), updatedLeaveRequest.getEmployeeId(), previousStatus, newStatus);

		try {
			eventPublisher.publishLeaveEvent(updatedLeaveRequest);
			log.info("Leave status update event published for leaveId={}", updatedLeaveRequest.getId());
		} catch (Exception publishException) {
			log.info("Failed to publish leave update event for leaveId={}, employeeId={}, status={}", 
					updatedLeaveRequest.getId(), updatedLeaveRequest.getEmployeeId(), newStatus, publishException);
		}

		return updatedLeaveRequest;
	}

	@Override
	@Transactional
	public List<LeaveRequest> getByEmployee(Long employeeId) {
		log.info("Fetching leave requests for employeeId={}", employeeId);

		employeeClient.validateEmployee(employeeId);

		List<LeaveRequest> employeeLeaveRequests = leaveRepository.findByEmployeeId(employeeId);

		if (employeeLeaveRequests == null || employeeLeaveRequests.isEmpty()) {
			log.warn("No leave records found for employeeId={}", employeeId);
			throw new ResourceNotFoundException("Employee not found or no leave records");
		}

		log.info("Found {} leave records for employeeId={}", employeeLeaveRequests.size(), employeeId);
		return employeeLeaveRequests;
	}

}
