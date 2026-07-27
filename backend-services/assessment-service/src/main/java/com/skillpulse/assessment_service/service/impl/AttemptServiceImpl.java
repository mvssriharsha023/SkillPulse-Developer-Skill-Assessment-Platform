package com.skillpulse.assessment_service.service.impl;

import com.skillpulse.assessment_service.client.UserClientService;
import com.skillpulse.assessment_service.entity.*;
import com.skillpulse.assessment_service.exceptions.NotAllowedException;
import com.skillpulse.assessment_service.exceptions.ResourceNotFoundException;
import com.skillpulse.assessment_service.mapper.AttemptMapper;
import com.skillpulse.assessment_service.mapper.QuestionMapper;
import com.skillpulse.assessment_service.messaging.AssessmentSubmissionProducer;
import com.skillpulse.assessment_service.model.*;
import com.skillpulse.assessment_service.repository.*;
import com.skillpulse.assessment_service.service.AttemptService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AttemptServiceImpl implements AttemptService {

    private final AssessmentRepository assessmentRepository;

    private final AssessmentAttemptRepository assessmentAttemptRepository;

    private final QuestionRepository questionRepository;

    private final AttemptAnswerRepository attemptAnswerRepository;

    private final AttemptDetailsViewRepository attemptDetailsViewRepository;

    private final UserClientService userClientService;

    private final QuestionMapper questionMapper;

    private final AttemptMapper attemptMapper;

    private final AssessmentSubmissionProducer assessmentSubmissionProducer;

    private final SimpleJdbcCall simpleJdbcCall;

    public AttemptServiceImpl(AssessmentRepository assessmentRepository, AssessmentAttemptRepository assessmentAttemptRepository, QuestionRepository questionRepository, AttemptAnswerRepository attemptAnswerRepository, AttemptDetailsViewRepository attemptDetailsViewRepository, UserClientService userClientService, QuestionMapper questionMapper, AttemptMapper attemptMapper, AssessmentSubmissionProducer assessmentSubmissionProducer, DataSource dataSource) {
        this.assessmentRepository = assessmentRepository;
        this.assessmentAttemptRepository = assessmentAttemptRepository;
        this.questionRepository = questionRepository;
        this.attemptAnswerRepository = attemptAnswerRepository;
        this.attemptDetailsViewRepository = attemptDetailsViewRepository;
        this.userClientService = userClientService;
        this.questionMapper = questionMapper;
        this.attemptMapper = attemptMapper;
        this.assessmentSubmissionProducer = assessmentSubmissionProducer;
        this.simpleJdbcCall = new SimpleJdbcCall(dataSource)
                .withProcedureName("SubmitAssessmentAttempt")
                .declareParameters(
                        new SqlParameter("p_attempt_id", Types.BIGINT),
                        new SqlParameter("p_time_taken_seconds", Types.INTEGER),
                        new SqlOutParameter("p_success", Types.BOOLEAN),
                        new SqlOutParameter("p_message", Types.VARCHAR),
                        new SqlOutParameter("p_correct_count", Types.INTEGER),
                        new SqlOutParameter("p_total_questions", Types.INTEGER)
                );
    }
    @Override
    public StartAttemptResponseDTO startAttempt(Long assessmentId, Long userId) {

        AssessmentEntity assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));
        if (!assessment.getStatus().equals("PUBLISHED")) {
            throw new NotAllowedException("Assessment is not PUBLISHED");
        }

        UserClientDTO userClientDTO = userClientService.getUserDetails(userId);

        if (userClientDTO.getRole().equals("COMPANY")) {
            throw new NotAllowedException("You are not allowed to perform this action");
        }

        if (userClientDTO.getStatus().equals("BANNED")) {
            throw new NotAllowedException("You are not BANNED to perform this action");
        }

        Optional<AssessmentAttemptEntity> assessmentAttempt = assessmentAttemptRepository.findByUserIdAndAssessmentId(userClientDTO.getId(), assessment.getId());

        if (assessmentAttempt.isPresent()) {
            throw new NotAllowedException("Assessment Attempt already exists");
        }

        AssessmentAttemptEntity newAttempt = new AssessmentAttemptEntity();
        newAttempt.setAssessmentId(assessment.getId());
        newAttempt.setUserId(userClientDTO.getId());
        newAttempt.setStatus("IN_PROGRESS");

        AssessmentAttemptEntity savedAssessmentEntity = assessmentAttemptRepository.save(newAttempt);

        List<QuestionEntity> questionEntities = questionRepository.findByAssessmentIdOrderByDisplayOrderAsc(assessment.getId());

        List<QuestionForAttemptDTO> questions = questionEntities.stream()
                .map(questionMapper::toQuestionForAttemptDTO)
                .toList();

        return StartAttemptResponseDTO.builder()
                .attemptId(savedAssessmentEntity.getId())
                .assessmentId(savedAssessmentEntity.getAssessmentId())
                .userId(savedAssessmentEntity.getUserId())
                .status(savedAssessmentEntity.getStatus())
                .startedAt(savedAssessmentEntity.getStartedAt())
                .timeLimitMinutes(assessment.getTimeLimitMinutes())
                .questions(questions)
                .build();
    }

    @Override
    public void saveAnswer(SubmitAnswerRequestDTO request) {
        AssessmentAttemptEntity assessmentAttempt = assessmentAttemptRepository.findById(request.getAttemptId())
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found"));

        if (!assessmentAttempt.getStatus().equals("IN_PROGRESS")) {
            throw new NotAllowedException("Attempt is not IN_PROGRESS");
        }

        Optional<AttemptAnswerEntity> attemptAnswerEntity = attemptAnswerRepository.findByAttemptIdAndQuestionId(assessmentAttempt.getId(), request.getQuestionId());

        if (attemptAnswerEntity.isPresent()) {
            // Update existing answer (allow changing answer)
            AttemptAnswerEntity attemptedAnswer = attemptAnswerEntity.get();
            attemptedAnswer.setSelectedOption(request.getSelectedOption());
            attemptAnswerRepository.save(attemptedAnswer);
        } else {
            AttemptAnswerEntity newAttemptAnswer = new AttemptAnswerEntity();
            newAttemptAnswer.setAttemptId(request.getAttemptId());
            newAttemptAnswer.setQuestionId(request.getQuestionId());
            newAttemptAnswer.setSelectedOption(request.getSelectedOption());
            attemptAnswerRepository.save(newAttemptAnswer);
        }
    }

    @Override
    public AttemptResultDTO submitAttempt(SubmitAttemptRequestDTO request) {

        AssessmentAttemptEntity assessmentAttempt = assessmentAttemptRepository.findById(request.getAttemptId())
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found"));

        if (assessmentAttempt.getStatus().equals("SUBMITTED")) {
            throw new NotAllowedException("Attempt is already SUBMITTED");
        }

        callSubmitProcedure(request.getAttemptId(), request.getTimeTakenSeconds(), this.simpleJdbcCall);

        AttemptDetailsView attemptDetails =
                attemptDetailsViewRepository.findById(request.getAttemptId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Attempt not found"));

        UserClientDTO userClientDTO = userClientService.getUserDetails(attemptDetails.getUserId());

        AssessmentSubmittedEvent assessmentSubmittedEvent = AssessmentSubmittedEvent.builder()
                .attemptId(attemptDetails.getAttemptId())
                .assessmentId(attemptDetails.getAssessmentId())
                .userId(attemptDetails.getUserId())
                .rawScore(attemptDetails.getRawScore())
                .maxScore(attemptDetails.getMaxScore())
                .scorePercentage(attemptDetails.getScorePercentage())
                .correctAnswers(attemptDetails.getCorrectAnswers())
                .totalQuestions(attemptDetails.getTotalQuestions())
                .passed(attemptDetails.getRawScore() >= attemptDetails.getPassingScore())
                .skillCategory(attemptDetails.getSkillCategory())
                .difficulty(attemptDetails.getDifficulty())
                .timeTakenSeconds(attemptDetails.getTimeTakenSeconds())
                .submittedAt(attemptDetails.getSubmittedAt())
                .userFullName(userClientDTO.getFullName())
                .build();

        assessmentSubmissionProducer.publish(assessmentSubmittedEvent);

        return attemptMapper.attemptToAttemptResultDTO(attemptDetails);
    }

    @Override
    public AttemptResultDTO getAttemptResult(Long attemptId) {
        AttemptDetailsView detailsView = attemptDetailsViewRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found"));

        return attemptMapper.attemptToAttemptResultDTO(detailsView);
    }

    @Override
    public Page<AttemptResultDTO> getUserAttempts(Long userId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<AttemptDetailsView> attemptDetailsViews = attemptDetailsViewRepository.findByUserId(userId, pageable);

        return attemptDetailsViews.map(attemptMapper::attemptToAttemptResultDTO);
    }

    public void callSubmitProcedure(Long attemptId, Integer timeTakenSeconds, SimpleJdbcCall simpleJdbcCall) {

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_attempt_id", attemptId)
                .addValue("p_time_taken_seconds", timeTakenSeconds);

        Map<String, Object> result = simpleJdbcCall.execute(params);

        AttemptSubmissionResult.builder()
                .success((Boolean) result.get("p_success"))
                .message((String) result.get("p_message"))
                .correctCount((Integer) result.get("p_correct_count"))
                .totalQuestions((Integer) result.get("p_total_questions"))
                .build();
    }
}
