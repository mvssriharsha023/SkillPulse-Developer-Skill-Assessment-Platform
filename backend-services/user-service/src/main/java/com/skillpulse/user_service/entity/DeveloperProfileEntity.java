package com.skillpulse.user_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "developer_profiles")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeveloperProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "experience_years", nullable = false)
    private Integer experienceYears;

    @Column(name = "primary_skill")
    private String primarySkill;

    @Column(name = "total_assessments", nullable = false)
    private Integer totalAssessments;

    @Column(name = "total_badges", nullable = false)
    private Integer totalBadges;

    @Column(name = "average_score", nullable = false)
    private Double averageScore;

    @Column(name = "rank_tier", nullable = false)
    private String rankTier;

    @Column(name = "last_active_at")
    private LocalDateTime lastActiveAt;
}
