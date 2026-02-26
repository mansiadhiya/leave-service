package com.company.leave.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.company.leave.entity.LeaveStatus;

class StatusUpdateDtoTest {

    @Test
    void setAndGetStatus() {
        StatusUpdateDto dto = new StatusUpdateDto();
        dto.setStatus(LeaveStatus.APPROVED);
        
        assertEquals(LeaveStatus.APPROVED, dto.getStatus());
    }
}
