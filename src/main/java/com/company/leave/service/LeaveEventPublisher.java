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

            log.info("Leave event published for request {}", leave.getId());

        } catch (Exception e){
            log.error("Failed to publish leave event", e);
        }
    }

}
