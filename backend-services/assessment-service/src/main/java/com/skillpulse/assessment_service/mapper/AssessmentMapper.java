package com.skillpulse.assessment_service.mapper;

import com.skillpulse.assessment_service.entity.AssessmentEntity;
import com.skillpulse.assessment_service.entity.AssessmentSummaryView;
import com.skillpulse.assessment_service.model.AssessmentResponseDTO;
import com.skillpulse.assessment_service.model.AssessmentSummaryDTO;
import org.springframework.stereotype.Component;

@Component
public class AssessmentMapper {

    public AssessmentResponseDTO assessmentToAssessmentResponse(AssessmentEntity assessmentEntity) {
        AssessmentResponseDTO assessmentResponseDTO = AssessmentResponseDTO.builder()
                .id(assessmentEntity.getId())
                .title(assessmentEntity.getTitle())
                .description(assessmentEntity.getDescription())
                .status(assessmentEntity.getStatus())
                .difficulty(assessmentEntity.getDifficulty())
                .skillCategory(assessmentEntity.getSkillCategory())
                .totalQuestions(assessmentEntity.getTotalQuestions())
                .timeLimitMinutes(assessmentEntity.getTimeLimitMinutes())
                .maxScore(assessmentEntity.getMaxScore())
                .passingScore(assessmentEntity.getPassingScore())
                .createdByUserId(assessmentEntity.getCreatedByUserId())
                .createdAt(assessmentEntity.getCreatedAt())
                .build();

        return assessmentResponseDTO;
    }

    public AssessmentSummaryDTO assessmentToAssessmentSummaryDTO(AssessmentSummaryView assessmentSummaryView) {

        return AssessmentSummaryDTO.builder()
                .id(assessmentSummaryView.getId())
                .title(assessmentSummaryView.getTitle())
                .description(assessmentSummaryView.getDescription())
                .skillCategory(assessmentSummaryView.getSkillCategory())
                .difficulty(assessmentSummaryView.getDifficulty())
                .timeLimitMinutes(assessmentSummaryView.getTotalLimitMinutes())
                .totalQuestions(assessmentSummaryView.getTotalQuestions())
                .maxScore(assessmentSummaryView.getMaxScore())
                .passingScore(assessmentSummaryView.getPassingScore())
                .status(assessmentSummaryView.getStatus())
                .totalAttempts(assessmentSummaryView.getTotalAttempts())
                .createdAt(assessmentSummaryView.getCreatedAt())
                .build();
    }
}
