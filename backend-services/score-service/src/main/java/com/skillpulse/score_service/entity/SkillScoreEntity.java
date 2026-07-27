package com.skillpulse.score_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "skill_scores",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "skill_category"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillScoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "skill_category", nullable = false)
    private String skillCategory;

    @Column(name = "assessments_taken", nullable = false)
    private Integer assessmentsTaken;

    @Column(name = "assessments_passed", nullable = false)
    private Integer assessmentsPassed;

    @Column(name = "best_score", nullable = false)
    private Double bestScore;

    @Column(name = "average_score", nullable = false)
    private Double averageScore;

    @Column(name = "last_assessed_at")
    private LocalDateTime lastAssessedAt;
}
