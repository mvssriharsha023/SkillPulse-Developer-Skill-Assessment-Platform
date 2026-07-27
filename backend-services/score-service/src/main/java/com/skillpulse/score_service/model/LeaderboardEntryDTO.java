package com.skillpulse.score_service.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntryDTO {

    private Integer rankPosition;

    private Long userId;

    private String fullName;

    private Long totalScore;

    private Integer assessmentsPassed;

    private Integer assessmentsTaken;

    private Double averagePercentage;

    private String bestCategory;
}
