package com.skillpulse.assessment_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {

    private Integer status;
    private String message;
    private LocalDateTime timestamp = LocalDateTime.now();

    public ErrorResponse(Integer status, String message) {
        this.status = status;
        this.message = message;
    }
}
