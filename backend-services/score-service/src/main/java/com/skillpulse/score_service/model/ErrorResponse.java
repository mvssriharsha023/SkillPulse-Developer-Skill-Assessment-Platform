package com.skillpulse.score_service.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private Integer status;

    private String message;

    private LocalDateTime timestamp =  LocalDateTime.now();

    public ErrorResponse(Integer status, String message) {
        this.status = status;
        this.message = message;
    }
}
