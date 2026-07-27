package com.skillpulse.score_service.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserScoreSummaryDTO {

    private Long userId;

    private Long totalScorePoints;

    private Integer assessmentsTaken;

    private Integer assessmentsPassed;

    private Double averagePercentage;

    private List<SkillScoreDTO> skillScores;

    private List<ScoreResponseDTO> recentScores;
}
