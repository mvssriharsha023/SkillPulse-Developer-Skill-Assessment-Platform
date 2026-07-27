package com.skillpulse.assessment_service.service;

import com.skillpulse.assessment_service.model.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AssessmentService {

    // Assessment CRUD
    AssessmentResponseDTO createAssessment(CreateAssessmentRequestDTO request);
    AssessmentResponseDTO getAssessmentById(Long id);
    Page<AssessmentSummaryDTO> getPublishedAssessments(String skillCategory, String difficulty, int page, int size);
    AssessmentResponseDTO publishAssessment(Long id);
    AssessmentResponseDTO archiveAssessment(Long id);

    // Questions
    QuestionResponseDTO addQuestion(AddQuestionRequestDTO request);
    List<QuestionResponseDTO> getQuestionsByAssessment(Long assessmentId);
    void deleteQuestion(Long questionId);
}
