package com.skillpulse.assessment_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "vw_attempt_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttemptDetailsView {

    @Id
    @Column(name = "attempt_id")
    private Long attemptId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "assessment_id")
    private Long assessmentId;

    @Column(name = "assessment_title")
    private String assessmentTitle;

    @Column(name = "skill_category")
    private String skillCategory;

    private String difficulty;

    @Column(name = "max_score")
    private Integer maxScore;

    @Column(name = "passing_score")
    private Integer passingScore;

    private String status;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "time_taken_seconds")
    private Integer timeTakenSeconds;

    @Column(name = "total_questions")
    private Integer totalQuestions;

    @Column(name = "total_answered")
    private Integer totalAnswered;

    @Column(name = "correct_answers")
    private Integer correctAnswers;

    @Column(name = "raw_score")
    private Integer rawScore;

    @Column(name = "score_percentage")
    private Double scorePercentage;
}
