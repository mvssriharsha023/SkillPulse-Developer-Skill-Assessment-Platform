package com.skillpulse.assessment_service.repository;

import com.skillpulse.assessment_service.entity.AssessmentAttemptEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssessmentAttemptRepository extends JpaRepository<AssessmentAttemptEntity, Long> {

    Optional<AssessmentAttemptEntity> findByUserIdAndAssessmentId(Long userId, Long assessmentId);
    boolean existsByUserIdAndAssessmentId(Long userId, Long assessmentId);
    List<AssessmentAttemptEntity> findByUserId(Long userId);
    List<AssessmentAttemptEntity> findByAssessmentId(Long assessmentId);
    Page<AssessmentAttemptEntity> findByUserId(Long userId, Pageable pageable);
}
