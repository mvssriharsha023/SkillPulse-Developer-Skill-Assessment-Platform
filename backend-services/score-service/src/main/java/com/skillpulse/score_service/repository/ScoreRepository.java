package com.skillpulse.score_service.repository;

import com.skillpulse.score_service.entity.ScoreEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<ScoreEntity, Long> {

    Optional<ScoreEntity> findByAttemptId(Long attemptId);

    boolean existsByAttemptId(Long attemptId);

    List<ScoreEntity> findByUserId(Long userId);

    List<ScoreEntity> findByUserIdOrderByScoredAtDesc(Long userId);

    List<ScoreEntity> findByUserIdAndSkillCategory(Long userId, String skillCategory);

    Page<ScoreEntity> findBySkillCategory(String skillCategory, Pageable pageable);

    @Query("SELECT COALESCE(SUM(s.rawScore), 0) FROM ScoreEntity s WHERE s.userId = :userId")
    Long sumRawScoreByUserId(@Param("userId") Long userId);

    @Query("SELECT COALESCE(AVG(s.scorePercentage), 0.0) FROM ScoreEntity s WHERE s.userId = :userId")
    Double avgScorePercentageByUserId(@Param("userId") Long userId);

    long countByUserId(Long userId);

    long countByUserIdAndPassedTrue(Long userId);
}