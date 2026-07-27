package com.skillpulse.user_service.respository;

import com.skillpulse.user_service.entity.BadgeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BadgeRepository extends JpaRepository<BadgeEntity, Long> {

    Optional<BadgeEntity> findByName(String name);
    List<BadgeEntity> findByCriteriaType(String criteriaType);
}
