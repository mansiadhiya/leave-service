package com.company.leave.dto;


import com.company.leave.entity.LeaveStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaveStatusChangedEvent {

	@NotNull(message = "Request ID is required")
    private Long requestId;

	@NotNull(message = "Employee name is required")
    private Long employeeName;

    @NotBlank(message = "Start date is required")
    private String startDate;

    @NotBlank(message = "End date is required")
    private String endDate;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status;

}
