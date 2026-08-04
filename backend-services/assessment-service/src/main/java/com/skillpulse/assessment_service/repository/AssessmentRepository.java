package com.skillpulse.assessment_service.repository;

import com.skillpulse.assessment_service.entity.AssessmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssessmentRepository extends JpaRepository<AssessmentEntity, Long> {
    
    List<AssessmentEntity> findByStatus(String status);
    Page<AssessmentEntity> findByCreatedByUserId(Long createdByUserId, Pageable pageable);
    Page<AssessmentEntity> findByCreatedByUserIdAndSkillCategory(Long createdByUserId, String skillCategory, Pageable pageable);
    Page<AssessmentEntity> findByCreatedByUserIdAndDifficulty(Long createdByUserId, String difficulty, Pageable pageable);
    Page<AssessmentEntity> findByCreatedByUserIdAndSkillCategoryAndDifficulty(Long createdByUserId, String skillCategory, String difficulty, Pageable pageable);
    List<AssessmentEntity> findBySkillCategoryAndStatus(String skillCategory, String status);
    List<AssessmentEntity> findByDifficultyAndStatus(String difficulty, String status);
    Page<AssessmentEntity> findByStatus(String status, Pageable pageable);
    Page<AssessmentEntity> findBySkillCategoryAndDifficultyAndStatus(
            String skillCategory, String difficulty, String status, Pageable pageable);
}
