package com.skillpulse.assessment_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "vw_assessment_summary")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentSummaryView {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "skill_category")
    private String skillCategory;

    @Column(name = "difficulty")
    private String difficulty;

    @Column(name = "total_limit_minutes")
    private Integer totalLimitMinutes;

    @Column(name = "total_questions")
    private Integer totalQuestions;

    @Column(name = "max_score")
    private Integer maxScore;

    @Column(name = "passing_score")
    private Integer passingScore;

    @Column(name = "status")
    private String status;

    @Column(name = "total_attempts")
    private Integer totalAttempts;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
