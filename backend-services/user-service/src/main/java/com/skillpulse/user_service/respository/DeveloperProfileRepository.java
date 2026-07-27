package com.skillpulse.user_service.respository;

import com.skillpulse.user_service.entity.DeveloperProfileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeveloperProfileRepository extends JpaRepository<DeveloperProfileEntity, Long> {

    Optional<DeveloperProfileEntity> findByUserId(Long userId);
    List<DeveloperProfileEntity> findByRankTierOrderByAverageScoreDesc(String rankTier);
    Page<DeveloperProfileEntity> findAllByOrderByAverageScoreDesc(Pageable pageable);
}
