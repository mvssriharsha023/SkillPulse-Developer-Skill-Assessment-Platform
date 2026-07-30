package com.skillpulse.user_service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttemptResultDTO {

    private Long attemptId;

    private Long assessmentId;

    private Long userId;

    private String status;

    private Integer rawScore;

    private Integer maxScore;

    private Double scorePercentage;

    private Integer totalAnswered;

    private Integer correctAnswers;

    private Integer totalQuestions;

    private Boolean passed;

    private Integer timeTakenSeconds;

    private LocalDateTime submittedAt;
}
