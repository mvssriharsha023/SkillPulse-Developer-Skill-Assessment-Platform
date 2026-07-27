package com.skillpulse.assessment_service.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartAttemptResponseDTO {

    private Long attemptId;

    private Long assessmentId;

    private Long userId;

    private String status;

    private LocalDateTime startedAt;

    private Integer timeLimitMinutes;

    private List<QuestionForAttemptDTO> questions;
}
