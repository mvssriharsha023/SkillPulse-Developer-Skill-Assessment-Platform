package com.skillpulse.user_service.service;

import com.skillpulse.user_service.entity.BadgeEntity;
import com.skillpulse.user_service.model.AttemptResultDTO;
import com.skillpulse.user_service.model.AwardBadgeEvent;
import com.skillpulse.user_service.model.BadgeResponseDTO;

import java.util.List;

public interface BadgeService {

    List<BadgeResponseDTO> getAllBadges();

    void evaluateAndAwardBadge(Long userId, List<AttemptResultDTO> attempts);
}
