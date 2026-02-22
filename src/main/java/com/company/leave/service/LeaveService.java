package com.company.leave.service;

import java.util.List;
import com.company.leave.dto.LeaveRequestDto;
import com.company.leave.entity.LeaveRequest;
import com.company.leave.entity.LeaveStatus;

public interface LeaveService {
	
	LeaveRequest createLeave(LeaveRequestDto dto);

    LeaveRequest updateStatus(Long id, LeaveStatus status);

    List<LeaveRequest> getByEmployee(Long empId);

}
