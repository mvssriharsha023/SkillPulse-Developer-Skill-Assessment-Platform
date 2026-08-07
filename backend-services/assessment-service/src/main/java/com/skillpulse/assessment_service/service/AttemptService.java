package com.skillpulse.assessment_service.service;

import com.skillpulse.assessment_service.model.AttemptResultDTO;
import com.skillpulse.assessment_service.model.StartAttemptResponseDTO;
import com.skillpulse.assessment_service.model.SubmitAnswerRequestDTO;
import com.skillpulse.assessment_service.model.SubmitAttemptRequestDTO;
import org.springframework.data.domain.Page;

public interface AttemptService {

    StartAttemptResponseDTO startAttempt(Long assessmentId, Long userId);
    void saveAnswer(SubmitAnswerRequestDTO request);
    AttemptResultDTO submitAttempt(SubmitAttemptRequestDTO request);
    AttemptResultDTO getAttemptResult(Long attemptId);
    Page<AttemptResultDTO> getUserAttempts(Long userId, int page, int size);
    AttemptResultDTO hasUserTakenAssessment(Long userId, Long assessmentId);

}
