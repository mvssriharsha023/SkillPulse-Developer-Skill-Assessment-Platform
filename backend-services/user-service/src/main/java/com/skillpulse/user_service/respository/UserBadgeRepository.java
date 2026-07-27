package com.skillpulse.user_service.respository;

import com.skillpulse.user_service.entity.UserBadgeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserBadgeRepository extends JpaRepository<UserBadgeEntity, Long> {

    List<UserBadgeEntity> findByUserId(Long userId);
    boolean existsByUserIdAndBadgeId(Long userId, Long badgeId);
    long countByUserId(Long userId);
}
