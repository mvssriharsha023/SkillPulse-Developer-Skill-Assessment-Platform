package com.skillpulse.assessment_service.repository;

import com.skillpulse.assessment_service.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {

    List<QuestionEntity> findByAssessmentIdOrderByDisplayOrderAsc(Long assessmentId);
    long countByAssessmentId(Long assessmentId);
}
