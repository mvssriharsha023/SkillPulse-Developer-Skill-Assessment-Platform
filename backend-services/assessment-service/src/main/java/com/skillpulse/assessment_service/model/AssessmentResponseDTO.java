package com.skillpulse.assessment_service.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentResponseDTO {

    private Long id;

    private String title;

    private String description;

    private String skillCategory;

    private String difficulty;

    private Integer timeLimitMinutes;

    private Integer totalQuestions;

    private Integer maxScore;

    private Integer passingScore;

    private Long createdByUserId;

    private String status;

    private LocalDateTime createdAt;
}
