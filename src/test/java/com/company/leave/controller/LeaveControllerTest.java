//package com.company.leave.controller;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//import java.time.LocalDate;
//import java.util.List;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import com.company.leave.config.JwtFilter;
//import com.company.leave.dto.LeaveRequestDto;
//import com.company.leave.entity.LeaveRequest;
//import com.company.leave.entity.LeaveStatus;
//import com.company.leave.service.LeaveService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//@WebMvcTest(LeaveController.class)
//class LeaveControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private LeaveService leaveService;
//
//    @MockBean
//    private JwtFilter jwtFilter;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void create_Success() throws Exception {
//        LeaveRequestDto dto = new LeaveRequestDto();
//        dto.setEmployeeId(1L);
//        dto.setStartDate(LocalDate.now().plusDays(1));
//        dto.setEndDate(LocalDate.now().plusDays(5));
//        dto.setReason("Vacation");
//
//        LeaveRequest leave = new LeaveRequest();
//        leave.setId(1L);
//
//        when(leaveService.createLeave(any())).thenReturn(leave);
//
//        mockMvc.perform(post("/api/leaves")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.success").value(true));
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void updateStatus_Success() throws Exception {
//        LeaveRequest leave = new LeaveRequest();
//        leave.setId(1L);
//        leave.setStatus(LeaveStatus.APPROVED);
//
//        when(leaveService.updateStatus(1L, LeaveStatus.APPROVED)).thenReturn(leave);
//
//        mockMvc.perform(put("/api/leaves/1/status")
//                .param("leaveStatus", "APPROVED"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true));
//    }
//
//    @Test
//    @WithMockUser(roles = "USER")
//    void list_Success() throws Exception {
//        when(leaveService.getByEmployee(1L)).thenReturn(List.of(new LeaveRequest()));
//
//        mockMvc.perform(get("/api/leaves/employee/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true));
//    }
//}
