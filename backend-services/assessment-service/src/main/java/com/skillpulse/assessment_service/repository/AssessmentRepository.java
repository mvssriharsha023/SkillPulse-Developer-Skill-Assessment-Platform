package com.skillpulse.assessment_service.repository;

import com.skillpulse.assessment_service.entity.AssessmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssessmentRepository extends JpaRepository<AssessmentEntity, Long> {
    
    List<AssessmentEntity> findByStatus(String status);
    List<AssessmentEntity> findBySkillCategoryAndStatus(String skillCategory, String status);
    List<AssessmentEntity> findByDifficultyAndStatus(String difficulty, String status);
    Page<AssessmentEntity> findByStatus(String status, Pageable pageable);
    Page<AssessmentEntity> findBySkillCategoryAndDifficultyAndStatus(
            String skillCategory, String difficulty, String status, Pageable pageable);
}
