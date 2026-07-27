package com.skillpulse.assessment_service.repository;

import com.skillpulse.assessment_service.entity.AssessmentSummaryView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssessmentSummaryViewRepository extends JpaRepository<AssessmentSummaryView, Long> {

    Page<AssessmentSummaryView> findBySkillCategoryAndDifficulty(String skillCategory, String difficulty, Pageable pageable);
}
