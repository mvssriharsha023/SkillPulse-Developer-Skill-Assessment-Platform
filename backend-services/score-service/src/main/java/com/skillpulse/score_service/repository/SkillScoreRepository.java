package com.skillpulse.score_service.repository;

import com.skillpulse.score_service.entity.SkillScoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillScoreRepository extends JpaRepository<SkillScoreEntity, Long> {

    List<SkillScoreEntity> findByUserId(Long userId);

    Optional<SkillScoreEntity> findByUserIdAndSkillCategory(Long userId, String skillCategory);

    List<SkillScoreEntity> findBySkillCategoryOrderByAverageScoreDesc(String skillCategory);
}