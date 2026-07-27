package com.skillpulse.score_service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserClientDeveloperProfileDTO {

    private Long id;

    private Long userId;

    private Integer experienceYears;

    private String primarySkill;

    private Integer totalAssessments;

    private Integer totalBadges;

    private Double averageScore;

    private String rankTier;

    private LocalDateTime lastActiveAt;
}