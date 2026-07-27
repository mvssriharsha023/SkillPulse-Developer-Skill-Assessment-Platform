package com.skillpulse.assessment_service.mapper;

import com.skillpulse.assessment_service.entity.QuestionEntity;
import com.skillpulse.assessment_service.model.QuestionForAttemptDTO;
import com.skillpulse.assessment_service.model.QuestionResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class QuestionMapper {

    public QuestionResponseDTO toQuestionResponse(QuestionEntity question) {
        return QuestionResponseDTO.builder()
                .id(question.getId())
                .assessmentId(question.getAssessmentId())
                .questionText(question.getQuestionText())
                .questionType(question.getQuestionType())
                .optionA(question.getOptionA())
                .optionB(question.getOptionB())
                .optionC(question.getOptionC())
                .optionD(question.getOptionD())
                .correctOption(question.getCorrectOption())
                .points(question.getPoints())
                .build();
    }

    public QuestionForAttemptDTO toQuestionForAttemptDTO(QuestionEntity question) {
        return QuestionForAttemptDTO.builder()
                .id(question.getId())
                .assessmentId(question.getAssessmentId())
                .questionText(question.getQuestionText())
                .questionType(question.getQuestionType())
                .optionA(question.getOptionA())
                .optionB(question.getOptionB())
                .optionC(question.getOptionC())
                .optionD(question.getOptionD())
                .points(question.getPoints())
                .displayOrder(question.getDisplayOrder())
                .build();
    }
}
