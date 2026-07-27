package com.skillpulse.user_service.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeveloperProfileResponseDTO {

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
