package com.skillpulse.score_service.mapper;

import com.skillpulse.score_service.entity.LeaderboardEntryEntity;
import com.skillpulse.score_service.entity.ScoreEntity;
import com.skillpulse.score_service.entity.SkillScoreEntity;
import com.skillpulse.score_service.model.LeaderboardEntryDTO;
import com.skillpulse.score_service.model.ScoreResponseDTO;
import com.skillpulse.score_service.model.SkillScoreDTO;
import org.springframework.stereotype.Component;

@Component
public class ScoreMapper {

    public ScoreResponseDTO toScoreResponseDTO(ScoreEntity entity) {
        return ScoreResponseDTO.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .assessmentId(entity.getAssessmentId())
                .attemptId(entity.getAttemptId())
                .rawScore(entity.getRawScore())
                .maxScore(entity.getMaxScore())
                .scorePercentage(entity.getScorePercentage())
                .correctAnswers(entity.getCorrectAnswers())
                .totalQuestions(entity.getTotalQuestions())
                .timeTakenSeconds(entity.getTimeTakenSeconds())
                .passed(entity.getPassed())
                .skillCategory(entity.getSkillCategory())
                .difficulty(entity.getDifficulty() != null
                        ? entity.getDifficulty() : null)
                .scoredAt(entity.getScoredAt())
                .build();
    }

    public LeaderboardEntryDTO toLeaderboardEntryDTO(LeaderboardEntryEntity entity) {
        return LeaderboardEntryDTO.builder()
                .rankPosition(entity.getRankPosition())
                .userId(entity.getUserId())
                .fullName(entity.getFullName())
                .totalScore(entity.getTotalScore())
                .assessmentsPassed(entity.getAssessmentsPassed())
                .assessmentsTaken(entity.getAssessmentsTaken())
                .averagePercentage(entity.getAveragePercentage())
                .bestCategory(entity.getBestCategory())
                .build();
    }

    public SkillScoreDTO toSkillScoreDTO(SkillScoreEntity entity) {
        return SkillScoreDTO.builder()
                .skillCategory(entity.getSkillCategory())
                .assessmentsTaken(entity.getAssessmentsTaken())
                .assessmentsPassed(entity.getAssessmentsPassed())
                .bestScore(entity.getBestScore())
                .averageScore(entity.getAverageScore())
                .lastAssessedAt(entity.getLastAssessedAt())
                .build();
    }
}