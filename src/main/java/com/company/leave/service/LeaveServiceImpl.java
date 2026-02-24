package com.company.leave.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.company.leave.client.EmployeeClient;
import com.company.leave.dto.LeaveRequestDto;
import com.company.leave.entity.*;
import com.company.leave.exception.BadRequestException;
import com.company.leave.exception.ResourceNotFoundException;
import com.company.leave.repository.LeaveRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LeaveServiceImpl implements LeaveService {

	private final LeaveRepository repo;
	private final EmployeeClient employeeClient;
	private final LeaveEventPublisher publisher;

	@Override
	public LeaveRequest createLeave(LeaveRequestDto dto) {
		log.info("Creating leave request for employeeId={}, startDate={}, endDate={}", 
				dto.getEmployeeId(), dto.getStartDate(), dto.getEndDate());

		validateDates(dto);

		employeeClient.validateEmployee(dto.getEmployeeId());

		LeaveRequest leave = LeaveRequest.builder().employeeId(dto.getEmployeeId()).startDate(dto.getStartDate())
				.endDate(dto.getEndDate()).reason(dto.getReason()).status(LeaveStatus.PENDING)
				.createdAt(LocalDateTime.now()).build();

		LeaveRequest saved = repo.save(leave);
		log.info("Leave request created successfully with leaveId={}, employeeId={}, status={}", 
				saved.getId(), saved.getEmployeeId(), saved.getStatus());

		try {
			publisher.publishLeaveEvent(saved);
			log.info("Leave event published successfully for leaveId={}", saved.getId());
		} catch (Exception e) {
			log.info("Failed to publish leave create event for leaveId={}, employeeId={}", 
					saved.getId(), saved.getEmployeeId(), e);
		}

		return saved;
	}

	private void validateDates(LeaveRequestDto dto) {
		log.debug("Validating dates for startDate={}, endDate={}", dto.getStartDate(), dto.getEndDate());
		if (dto.getEndDate().isBefore(dto.getStartDate())) {
			log.warn("Invalid date range: endDate={} is before startDate={}", dto.getEndDate(), dto.getStartDate());
			throw new BadRequestException("End date must be after start date");
		}
	}

	@Override
	public LeaveRequest updateStatus(Long id, LeaveStatus status) {
		log.info("Updating leave status for leaveId={}, newStatus={}", id, status);

		LeaveRequest leave = repo.findById(id)
				.orElseThrow(() -> {
					log.info("Leave not found with leaveId={}", id);
					return new ResourceNotFoundException("Leave not found");
				});

		LeaveStatus oldStatus = leave.getStatus();
		leave.setStatus(status);
		LeaveRequest updated = repo.save(leave);
		log.info("Leave status updated successfully for leaveId={}, employeeId={}, oldStatus={}, newStatus={}", 
				updated.getId(), updated.getEmployeeId(), oldStatus, status);

		try {
			publisher.publishLeaveEvent(updated);
			log.info("Leave status update event published for leaveId={}", updated.getId());
		} catch (Exception e) {
			log.info("Failed to publish leave update event for leaveId={}, employeeId={}, status={}", 
					updated.getId(), updated.getEmployeeId(), status, e);
		}

		return updated;
	}

	@Override
	@Transactional
	public List<LeaveRequest> getByEmployee(Long empId) {
		log.info("Fetching leave requests for employeeId={}", empId);

		employeeClient.validateEmployee(empId);

		List<LeaveRequest> leaves = repo.findByEmployeeId(empId);

		if (leaves == null || leaves.isEmpty()) {
			log.warn("No leave records found for employeeId={}", empId);
			throw new ResourceNotFoundException("Employee not found or no leave records");
		}

		log.info("Found {} leave records for employeeId={}", leaves.size(), empId);
		return leaves;
	}

}
