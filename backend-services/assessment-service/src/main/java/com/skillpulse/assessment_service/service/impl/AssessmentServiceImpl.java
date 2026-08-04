package com.skillpulse.assessment_service.service.impl;

import com.skillpulse.assessment_service.client.UserClientService;
import com.skillpulse.assessment_service.entity.AssessmentEntity;
import com.skillpulse.assessment_service.entity.AssessmentSummaryView;
import com.skillpulse.assessment_service.entity.QuestionEntity;
import com.skillpulse.assessment_service.exceptions.NotAllowedException;
import com.skillpulse.assessment_service.exceptions.ResourceNotFoundException;
import com.skillpulse.assessment_service.mapper.AssessmentMapper;
import com.skillpulse.assessment_service.mapper.QuestionMapper;
import com.skillpulse.assessment_service.model.*;
import com.skillpulse.assessment_service.repository.AssessmentRepository;
import com.skillpulse.assessment_service.repository.AssessmentSummaryViewRepository;
import com.skillpulse.assessment_service.repository.QuestionRepository;
import com.skillpulse.assessment_service.service.AssessmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AssessmentServiceImpl implements AssessmentService {

    private final UserClientService userClientService;

    private final AssessmentRepository assessmentRepository;

    private final QuestionRepository questionRepository;

    private final AssessmentSummaryViewRepository assessmentSummaryViewRepository;

    private final AssessmentMapper assessmentMapper;

    private final QuestionMapper questionMapper;

    public AssessmentServiceImpl(UserClientService userClientService,  AssessmentRepository assessmentRepository, QuestionRepository questionRepository, AssessmentSummaryViewRepository assessmentSummaryViewRepository, AssessmentMapper assessmentMapper, QuestionMapper questionMapper) {
        this.userClientService = userClientService;
        this.assessmentRepository = assessmentRepository;
        this.questionRepository = questionRepository;
        this.assessmentSummaryViewRepository = assessmentSummaryViewRepository;
        this.assessmentMapper = assessmentMapper;
        this.questionMapper = questionMapper;
    }
    @Override
    public AssessmentResponseDTO createAssessment(CreateAssessmentRequestDTO request) {

        UserClientDTO userClientDTO = userClientService.getUserDetails(request.getCreatedByUserId());

        if (userClientDTO.getRole().equals("DEVELOPER")) {
            throw new NotAllowedException("Developers are not allowed to create assessment");
        }

        AssessmentEntity assessment = AssessmentEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .skillCategory(request.getSkillCategory())
                .difficulty(request.getDifficulty())
                .timeLimitMinutes(request.getTimeLimitMinutes())
                .passingScore(request.getPassingScore())
                .createdByUserId(request.getCreatedByUserId())
                .status("DRAFT")
                .build();

        AssessmentEntity savedAssessment = assessmentRepository.save(assessment);


        return assessmentMapper.assessmentToAssessmentResponse(savedAssessment);
    }

    @Override
    public AssessmentResponseDTO getAssessmentById(Long id) {

        AssessmentEntity assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found with id " + id));

        return assessmentMapper.assessmentToAssessmentResponse(assessment);
    }

    @Override
    public AssessmentResponseDTO updateAssessment(UpdateAssessmentRequestDTO requestDTO) {
        AssessmentEntity assessment = assessmentRepository.findById(requestDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found with id " + requestDTO.getId()));

        if (assessment.getStatus().equals("PUBLISHED")) {
            throw new NotAllowedException("Published assessment is not allowed to be updated");
        }
        assessment.setTitle(requestDTO.getTitle());
        assessment.setDescription(requestDTO.getDescription());
        assessment.setSkillCategory(requestDTO.getSkillCategory());
        assessment.setDifficulty(requestDTO.getDifficulty());
        assessment.setTimeLimitMinutes(requestDTO.getTimeLimitMinutes());
        assessment.setPassingScore(requestDTO.getPassingScore());

        assessmentRepository.save(assessment);

        return assessmentMapper.assessmentToAssessmentResponse(assessment);
    }

    @Override
    public Page<AssessmentSummaryDTO> getPublishedAssessments(
            String skillCategory,
            String difficulty,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        Page<AssessmentSummaryView> assessmentSummaryViewPage;

        if (skillCategory != null && !skillCategory.isEmpty()
                && difficulty != null && !difficulty.isEmpty()) {

            assessmentSummaryViewPage =
                    assessmentSummaryViewRepository
                            .findBySkillCategoryAndDifficulty(
                                    skillCategory,
                                    difficulty,
                                    pageable
                            );

        } else if (skillCategory != null && !skillCategory.isEmpty()) {

            assessmentSummaryViewPage =
                    assessmentSummaryViewRepository
                            .findBySkillCategory(
                                    skillCategory,
                                    pageable
                            );

        } else if (difficulty != null && !difficulty.isEmpty()) {

            assessmentSummaryViewPage =
                    assessmentSummaryViewRepository
                            .findByDifficulty(
                                    difficulty,
                                    pageable
                            );

        } else {

            assessmentSummaryViewPage =
                    assessmentSummaryViewRepository.findAll(pageable);
        }

        return assessmentSummaryViewPage
                .map(assessmentMapper::assessmentToAssessmentSummaryDTO);
    }

    @Override
    public AssessmentResponseDTO publishAssessment(Long id) {
        AssessmentEntity assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));

        if (!assessment.getStatus().equals("DRAFT")) {
            throw new NotAllowedException("Assessment status is not DRAFT");
        }

        if (assessment.getTotalQuestions() < 1) {
            throw new NotAllowedException("Total questions cannot be less than 1");
        }

        if (assessment.getPassingScore() > assessment.getMaxScore()) {
            throw new NotAllowedException("Passing score cannot be greater than max score");
        }

        assessment.setStatus("PUBLISHED");

        AssessmentEntity savedAssessment = assessmentRepository.save(assessment);

        return assessmentMapper.assessmentToAssessmentResponse(savedAssessment);
    }

    @Override
    public AssessmentResponseDTO archiveAssessment(Long id) {

        AssessmentEntity assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));

        if (!assessment.getStatus().equals("PUBLISHED")) {
            throw new NotAllowedException("Assessment is already published");
        }

        assessment.setStatus("ARCHIVED");

        AssessmentEntity savedAssessment = assessmentRepository.save(assessment);

        return assessmentMapper.assessmentToAssessmentResponse(savedAssessment);
    }

    @Override
    public Page<AssessmentResponseDTO> getUserAssessments(Long userId, String skillCategory, String difficulty, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        log.info("Params received {} {} {}", userId, skillCategory, difficulty);

        boolean hasSkillCategory = skillCategory != null && !skillCategory.isBlank();
        boolean hasDifficulty = difficulty != null && !difficulty.isBlank();

        String filterType = (hasSkillCategory ? "S" : "") + (hasDifficulty ? "D" : "");

        Page<AssessmentEntity> assessmentEntities = switch (filterType) {
            case "SD" -> assessmentRepository
                    .findByCreatedByUserIdAndSkillCategoryAndDifficulty(
                            userId, skillCategory, difficulty, pageable);
            case "S" -> assessmentRepository
                    .findByCreatedByUserIdAndSkillCategory(
                            userId, skillCategory, pageable);
            case "D" -> assessmentRepository
                    .findByCreatedByUserIdAndDifficulty(
                            userId, difficulty, pageable);
            default -> assessmentRepository
                    .findByCreatedByUserId(userId, pageable);
        };

        return assessmentEntities
                .map(assessmentMapper::assessmentToAssessmentResponse);
    }

    @Override
    public QuestionResponseDTO addQuestion(AddQuestionRequestDTO request) {

        AssessmentEntity assessment = assessmentRepository.findById(request.getAssessmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found!"));

        QuestionEntity question = QuestionEntity.builder()
                .assessmentId(assessment.getId())
                .questionText(request.getQuestionText())
                .questionType(request.getQuestionType())
                .optionA(request.getOptionA())
                .optionB(request.getOptionB())
                .optionC(request.getOptionC())
                .optionD(request.getOptionD())
                .correctOption(request.getCorrectOption())
                .points(request.getPoints())
                .displayOrder(request.getDisplayOrder())
                .build();

        QuestionEntity savedQuestion = questionRepository.save(question);

        return questionMapper.toQuestionResponse(savedQuestion);
    }

    @Override
    public List<QuestionResponseDTO> getQuestionsByAssessment(Long assessmentId) {
        List<QuestionEntity> questions = questionRepository.findByAssessmentIdOrderByDisplayOrderAsc(assessmentId);

        return questions.stream().map(questionMapper::toQuestionResponse).toList();
    }

    @Override
    public void deleteQuestion(Long questionId) {
        QuestionEntity question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found!"));

        questionRepository.delete(question);
    }
}
