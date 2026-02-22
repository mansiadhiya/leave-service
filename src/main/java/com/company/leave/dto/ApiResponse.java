package com.company.leave.dto;

import java.time.LocalDateTime;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
	
	private boolean success;
    private String message;
    private T data;
    private LocalDateTime time;

    public static <T> ApiResponse<T> success(String msg, T data) {
        return new ApiResponse<>(true, msg, data, LocalDateTime.now());
    }

    public static ApiResponse<?> error(String msg) {
        return new ApiResponse<>(false, msg, null, LocalDateTime.now());
    }

}
