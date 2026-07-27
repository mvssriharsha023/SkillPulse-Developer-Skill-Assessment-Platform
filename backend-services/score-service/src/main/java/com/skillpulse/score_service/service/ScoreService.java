package com.skillpulse.score_service.service;

import com.skillpulse.score_service.model.ScoreResponseDTO;
import com.skillpulse.score_service.model.SkillScoreDTO;
import com.skillpulse.score_service.model.UserScoreSummaryDTO;
import com.skillpulse.score_service.model.AssessmentSubmittedEvent;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ScoreService {

    boolean isAlreadyScored(Long attemptId);

    void processScore(AssessmentSubmittedEvent event);

    ScoreResponseDTO getScoreByAttemptId(Long attemptId);

    List<ScoreResponseDTO> getScoresByUserId(Long userId);

    UserScoreSummaryDTO getUserScoreSummary(Long userId);

    List<SkillScoreDTO> getUserSkillScores(Long userId);

    Page<ScoreResponseDTO> getScoresBySkillCategory(String skillCategory, int page, int size);
}