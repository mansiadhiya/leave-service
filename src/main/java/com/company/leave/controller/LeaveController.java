package com.company.leave.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.company.leave.dto.ApiResponse;
import com.company.leave.dto.LeaveRequestDto;
import com.company.leave.entity.LeaveStatus;
import com.company.leave.service.LeaveService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
@Validated
public class LeaveController {

	private final LeaveService leaveService;

	@PostMapping
	public ResponseEntity<ApiResponse<Object>> create(@Valid @RequestBody LeaveRequestDto leaveRequestDto) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("Leave created", leaveService.createLeave(leaveRequestDto)));
	}

	@PutMapping("/{id}/status")
	public ApiResponse<Object> updateStatus(@PathVariable("id") Long leaveId, @RequestParam LeaveStatus leaveStatus) {

		return ApiResponse.success("Status updated", leaveService.updateStatus(leaveId, leaveStatus));
	}

	@GetMapping("/employee/{empId}")
	public ApiResponse<Object> list(@PathVariable("empId") Long employeeId) {
		return ApiResponse.success("Employee leaves", leaveService.getByEmployee(employeeId));
	}

}
