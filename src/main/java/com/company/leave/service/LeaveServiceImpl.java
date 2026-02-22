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

	@Override
	public LeaveRequest createLeave(LeaveRequestDto dto) {

		validateDates(dto);

		employeeClient.validateEmployee(dto.getEmployeeId());

		LeaveRequest leave = LeaveRequest.builder().employeeId(dto.getEmployeeId()).startDate(dto.getStartDate())
				.endDate(dto.getEndDate()).reason(dto.getReason()).status(LeaveStatus.PENDING)
				.createdAt(LocalDateTime.now()).build();

		return repo.save(leave);
	}

	private void validateDates(LeaveRequestDto dto) {
		if (dto.getEndDate().isBefore(dto.getStartDate()))
			throw new BadRequestException("End date must be after start date");
	}

	@Override
	public LeaveRequest updateStatus(Long id, LeaveStatus status) {

		LeaveRequest leave = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Leave not found"));

		leave.setStatus(status);
		return repo.save(leave);
	}

	@Override
	@Transactional
	public List<LeaveRequest> getByEmployee(Long empId) {
		List<LeaveRequest> leaves = repo.findByEmployeeId(empId);

		if (leaves.isEmpty()) {
			throw new ResourceNotFoundException("Employee not found or no leave records");
		}

		return leaves;
	}

}
