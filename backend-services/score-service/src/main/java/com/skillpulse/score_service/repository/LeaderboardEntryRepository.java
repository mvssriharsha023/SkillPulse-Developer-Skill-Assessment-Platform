package com.skillpulse.score_service.repository;

import com.skillpulse.score_service.entity.LeaderboardEntryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaderboardEntryRepository extends JpaRepository<LeaderboardEntryEntity, Long> {

    Optional<LeaderboardEntryEntity> findByUserId(Long userId);

    List<LeaderboardEntryEntity> findTop10ByOrderByRankPositionAsc();

    List<LeaderboardEntryEntity> findAllByOrderByTotalScoreDesc();

    Page<LeaderboardEntryEntity> findAllByOrderByRankPositionAsc(Pageable pageable);
}