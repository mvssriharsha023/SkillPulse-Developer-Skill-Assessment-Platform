package com.skillpulse.score_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "scores")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "assessment_id", nullable = false)
    private Long assessmentId;

    @Column(name = "attempt_id", nullable = false)
    private Long attemptId;

    @Column(name = "raw_score", nullable = false)
    private Integer rawScore;

    @Column(name = "max_score", nullable = false)
    private Integer maxScore;

    @Column(name = "score_percentage", nullable = false)
    private Double scorePercentage;

    @Column(name = "correct_answers", nullable = false)
    private Integer correctAnswers;

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;

    @Column(name = "time_taken_seconds")
    private Integer timeTakenSeconds;

    @Column(name = "passed", nullable = false)
    private Boolean passed;

    @Column(name = "skill_category", nullable = false)
    private String skillCategory;

    @Column(name = "difficulty", nullable = false)
    private String difficulty;

    @Column(name = "scored_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime scoredAt;
}
