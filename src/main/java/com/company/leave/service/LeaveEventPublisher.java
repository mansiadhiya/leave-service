package com.company.leave.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.company.leave.dto.LeaveStatusChangedEvent;
import com.company.leave.entity.LeaveRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveEventPublisher {
	
	private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper mapper;

    public void publishLeaveEvent(LeaveRequest leave){
        log.info("Publishing leave event for leaveId={}, employeeId={}, status={}", 
                leave.getId(), leave.getEmployeeId(), leave.getStatus());

        try{

            LeaveStatusChangedEvent event =
                    LeaveStatusChangedEvent.builder()
                            .requestId(leave.getId())
                            .employeeName(leave.getEmployeeId())
                            .startDate(leave.getStartDate().toString())
                            .endDate(leave.getEndDate().toString())
                            .status(leave.getStatus())
                            .build();

            String payload = mapper.writeValueAsString(event);

            rabbitTemplate.convertAndSend(
                    "notification.exchange",
                    "leave.status.changed",
                    payload
            );

            log.info("Leave event published successfully for leaveId={}, employeeId={}", 
                    leave.getId(), leave.getEmployeeId());

        } catch (Exception e){
            log.info("Failed to publish leave event for leaveId={}, employeeId={}", 
                    leave.getId(), leave.getEmployeeId(), e);
            throw e;
        }
    }

}
