package com.skillpulse.score_service.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreResponseDTO {

    private Long id;

    private Long userId;

    private Long assessmentId;

    private Long attemptId;

    private Integer rawScore;

    private Integer maxScore;

    private Double scorePercentage;

    private Integer correctAnswers;

    private Integer totalQuestions;

    private Integer timeTakenSeconds;

    private Boolean passed;

    private String skillCategory;

    private String difficulty;

    private LocalDateTime scoredAt;
}
