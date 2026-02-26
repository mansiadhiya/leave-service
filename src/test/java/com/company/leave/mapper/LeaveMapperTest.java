package com.company.leave.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.company.leave.dto.LeaveRequestDto;
import com.company.leave.entity.LeaveRequest;

class LeaveMapperTest {

    private final LeaveMapper leaveMapper = Mappers.getMapper(LeaveMapper.class);

    @Test
    void toEntity_WithValidDto_MapsCorrectly() {
        LeaveRequestDto dto = new LeaveRequestDto();
        dto.setEmployeeId(1L);
        dto.setStartDate(LocalDate.now().plusDays(1));
        dto.setEndDate(LocalDate.now().plusDays(5));
        dto.setReason("Vacation");

        LeaveRequest entity = leaveMapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getEmployeeId()).isEqualTo(1L);
        assertThat(entity.getStartDate()).isEqualTo(dto.getStartDate());
        assertThat(entity.getEndDate()).isEqualTo(dto.getEndDate());
        assertThat(entity.getReason()).isEqualTo("Vacation");
    }
}
