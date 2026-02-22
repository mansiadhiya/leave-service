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

	private final LeaveService service;

	@PostMapping
	public ResponseEntity<ApiResponse<?>> create(@Valid @RequestBody LeaveRequestDto dto) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("Leave created", service.createLeave(dto)));
	}

	@PutMapping("/{id}/status")
	public ApiResponse<?> updateStatus(@PathVariable Long id, @RequestParam LeaveStatus status) {

		return ApiResponse.success("Status updated", service.updateStatus(id, status));
	}

	@GetMapping("/employee/{empId}")
	public ApiResponse<?> list(@PathVariable Long empId) {
		return ApiResponse.success("Employee leaves", service.getByEmployee(empId));
	}

}
