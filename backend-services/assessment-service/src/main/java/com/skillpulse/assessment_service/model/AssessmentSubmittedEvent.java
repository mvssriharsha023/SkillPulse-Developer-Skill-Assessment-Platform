package com.skillpulse.assessment_service.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentSubmittedEvent {

    private Long attemptId;

    private Long assessmentId;

    private Long userId;

    private Integer rawScore;

    private Integer maxScore;

    private Double scorePercentage;

    private Integer correctAnswers;

    private Integer totalQuestions;

    private Boolean passed;

    private String skillCategory;

    private String difficulty;

    private Integer timeTakenSeconds;

    private LocalDateTime submittedAt;

    private String userFullName;
}
