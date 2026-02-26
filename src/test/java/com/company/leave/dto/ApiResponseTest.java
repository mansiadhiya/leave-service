package com.company.leave.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ApiResponseTest {

    @Test
    void success_CreatesSuccessResponse() {
        ApiResponse<String> response = ApiResponse.success("Success", "data");

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Success");
        assertThat(response.getData()).isEqualTo("data");
        assertThat(response.getTime()).isNotNull();
    }

    @Test
    void error_CreatesErrorResponse() {
        ApiResponse<Object> response = ApiResponse.error("Error occurred");

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Error occurred");
        assertThat(response.getData()).isNull();
        assertThat(response.getTime()).isNotNull();
    }

    @Test
    void constructor_WithAllFields_CreatesResponse() {
        ApiResponse<Integer> response = new ApiResponse<>(true, "Test", 100, null);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Test");
        assertThat(response.getData()).isEqualTo(100);
    }
}
