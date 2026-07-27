package com.skillpulse.score_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "leaderboard_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaderboardEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "total_score", nullable = false)
    private Long totalScore;

    @Column(name = "assessments_passed", nullable = false)
    private Integer assessmentsPassed;

    @Column(name = "assessments_taken", nullable = false)
    private Integer assessmentsTaken;

    @Column(name = "average_percentage", nullable = false)
    private Double averagePercentage;

    @Column(name = "best_category")
    private String bestCategory;

    @Column(name = "rank_position")
    private Integer rankPosition;

    @Column(name = "last_updated")
    @UpdateTimestamp
    private LocalDateTime lastUpdated;
}
