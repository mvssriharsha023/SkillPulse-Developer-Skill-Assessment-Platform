package com.skillpulse.assessment_service.mapper;

import com.skillpulse.assessment_service.entity.AttemptDetailsView;
import com.skillpulse.assessment_service.model.AttemptResultDTO;
import org.springframework.stereotype.Component;

@Component
public class AttemptMapper {

    public AttemptResultDTO attemptToAttemptResultDTO(AttemptDetailsView attemptDetailsView) {
        return AttemptResultDTO.builder()
                .attemptId(attemptDetailsView.getAttemptId())
                .assessmentId(attemptDetailsView.getAssessmentId())
                .userId(attemptDetailsView.getUserId())
                .status(attemptDetailsView.getStatus())
                .rawScore(attemptDetailsView.getRawScore())
                .maxScore(attemptDetailsView.getMaxScore())
                .scorePercentage(attemptDetailsView.getScorePercentage())
                .totalAnswered(attemptDetailsView.getTotalAnswered())
                .totalQuestions(attemptDetailsView.getTotalQuestions())
                .correctAnswers(attemptDetailsView.getCorrectAnswers())
                .passed(
                        attemptDetailsView.getRawScore() >=
                                attemptDetailsView.getPassingScore()
                )
                .timeTakenSeconds(attemptDetailsView.getTimeTakenSeconds())
                .submittedAt(attemptDetailsView.getSubmittedAt())
                .build();
    }
}
