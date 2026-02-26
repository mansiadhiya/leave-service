package com.company.leave.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.company.leave.entity.LeaveRequest;
import com.company.leave.entity.LeaveStatus;

@DataJpaTest
class LeaveRepositoryTest {

    @Autowired
    private LeaveRepository leaveRepository;

    private LeaveRequest leaveRequest;

    @BeforeEach
    void setUp() {
        leaveRepository.deleteAll();

        leaveRequest = LeaveRequest.builder()
                .employeeId(1L)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(5))
                .status(LeaveStatus.PENDING)
                .reason("Vacation")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void save_WithValidLeave_SavesSuccessfully() {
        LeaveRequest saved = leaveRepository.save(leaveRequest);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmployeeId()).isEqualTo(1L);
        assertThat(saved.getStatus()).isEqualTo(LeaveStatus.PENDING);
    }

    @Test
    void findByEmployeeId_WithExistingEmployee_ReturnsLeaves() {
        leaveRepository.save(leaveRequest);

        List<LeaveRequest> leaves = leaveRepository.findByEmployeeId(1L);

        assertThat(leaves).hasSize(1);
        assertThat(leaves.get(0).getEmployeeId()).isEqualTo(1L);
    }

    @Test
    void findByEmployeeId_WithNoLeaves_ReturnsEmpty() {
        List<LeaveRequest> leaves = leaveRepository.findByEmployeeId(999L);

        assertThat(leaves).isEmpty();
    }

    @Test
    void findById_WithExistingId_ReturnsLeave() {
        LeaveRequest saved = leaveRepository.save(leaveRequest);

        assertThat(leaveRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void deleteById_WithExistingId_DeletesLeave() {
        LeaveRequest saved = leaveRepository.save(leaveRequest);

        leaveRepository.deleteById(saved.getId());

        assertThat(leaveRepository.findById(saved.getId())).isEmpty();
    }
}
