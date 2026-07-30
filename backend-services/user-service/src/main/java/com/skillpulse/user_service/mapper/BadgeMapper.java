package com.skillpulse.user_service.mapper;

import com.skillpulse.user_service.entity.BadgeEntity;
import com.skillpulse.user_service.model.BadgeResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class BadgeMapper {

    public BadgeResponseDTO badgeEntityToResponseDTO(BadgeEntity badgeEntity) {

        return BadgeResponseDTO.builder()
                .id(badgeEntity.getId())
                .name(badgeEntity.getName())
                .description(badgeEntity.getDescription())
                .iconUrl(badgeEntity.getIconUrl())
                .criteriaType(badgeEntity.getCriteriaType())
                .criteriaValue(badgeEntity.getCriteriaValue())
                .build();
    }
}
