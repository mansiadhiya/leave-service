package com.company.leave.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.company.leave.entity.LeaveRequest;

public interface LeaveRepository extends JpaRepository<LeaveRequest, Long>{
	List<LeaveRequest> findByEmployeeId(Long employeeId);

}
