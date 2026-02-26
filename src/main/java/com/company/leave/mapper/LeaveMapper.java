package com.company.leave.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.company.leave.dto.LeaveRequestDto;
import com.company.leave.entity.LeaveRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LeaveMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    LeaveRequest toEntity(LeaveRequestDto dto);
}
