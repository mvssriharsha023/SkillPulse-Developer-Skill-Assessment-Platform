package com.skillpulse.score_service.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillScoreDTO {

    private String skillCategory;

    private Integer assessmentsTaken;

    private Integer assessmentsPassed;

    private Double bestScore;

    private Double averageScore;

    private LocalDateTime lastAssessedAt;
}
