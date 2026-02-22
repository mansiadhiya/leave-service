package com.company.leave.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leave_requests")
@Getter
@Setter 
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class LeaveRequest {
	
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private Long employeeId;

	    private LocalDate startDate;
	    private LocalDate endDate;

	    @Enumerated(EnumType.STRING)
	    private LeaveStatus status;

	    private String reason;

	    private LocalDateTime createdAt;

}
