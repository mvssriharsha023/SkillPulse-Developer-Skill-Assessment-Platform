package com.skillpulse.user_service.mapper;

import com.skillpulse.user_service.entity.BadgeEntity;
import com.skillpulse.user_service.entity.DeveloperProfileEntity;
import com.skillpulse.user_service.entity.UserEntity;
import com.skillpulse.user_service.model.BadgeResponseDTO;
import com.skillpulse.user_service.model.DeveloperProfileResponseDTO;
import com.skillpulse.user_service.model.UserResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDTO userToUserResponseDTO(UserEntity user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .bio(user.getBio())
                .githubUrl(user.getGithubUrl())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public DeveloperProfileResponseDTO developerProfileEntityToResponseDTO(DeveloperProfileEntity developerProfile) {
        return DeveloperProfileResponseDTO.builder()
                .id(developerProfile.getId())
                .userId(developerProfile.getUserId())
                .experienceYears(developerProfile.getExperienceYears())
                .primarySkill(developerProfile.getPrimarySkill())
                .totalAssessments(developerProfile.getTotalAssessments())
                .totalBadges(developerProfile.getTotalBadges())
                .averageScore(developerProfile.getAverageScore())
                .rankTier(developerProfile.getRankTier())
                .lastActiveAt(developerProfile.getLastActiveAt())
                .build();
    }

    public BadgeResponseDTO badgeEntityToResponseDTO(BadgeEntity badge) {
        return BadgeResponseDTO.builder()
                .id(badge.getId())
                .name(badge.getName())
                .description(badge.getDescription())
                .iconUrl(badge.getIconUrl())
                .criteriaType(badge.getCriteriaType())
                .criteriaValue(badge.getCriteriaValue())
                .build();
    }
}
