package com.skillpulse.user_service.service.impl;

import com.skillpulse.user_service.entity.BadgeEntity;
import com.skillpulse.user_service.mapper.BadgeMapper;
import com.skillpulse.user_service.model.AttemptResultDTO;
import com.skillpulse.user_service.model.AwardBadgeRequestDTO;
import com.skillpulse.user_service.model.BadgeResponseDTO;
import com.skillpulse.user_service.respository.BadgeRepository;
import com.skillpulse.user_service.respository.UserBadgeRepository;
import com.skillpulse.user_service.service.BadgeService;
import com.skillpulse.user_service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BadgeServiceImpl implements BadgeService {

    private final UserService userService;

    private final BadgeRepository badgeRepository;

    private final UserBadgeRepository userBadgeRepository;

    private final BadgeMapper badgeMapper;

    public BadgeServiceImpl(UserService userService, BadgeRepository badgeRepository, UserBadgeRepository userBadgeRepository, BadgeMapper badgeMapper) {
        this.userService = userService;
        this.badgeRepository = badgeRepository;
        this.userBadgeRepository = userBadgeRepository;
        this.badgeMapper = badgeMapper;
    }

    @Override
    public List<BadgeResponseDTO> getAllBadges() {

        List<BadgeEntity> badgeEntity = badgeRepository.findAll();

        return badgeEntity.stream().map(badgeMapper::badgeEntityToResponseDTO).toList();
    }

    @Override
    public void evaluateAndAwardBadge(Long userId, List<AttemptResultDTO> attempts) {

        if (attempts == null || attempts.isEmpty()) {
            return;
        }

        List<BadgeResponseDTO> allBadges = getAllBadges();

        int assessmentCount = attempts.size();

        double averageScore = attempts.stream()
                .mapToDouble(AttemptResultDTO::getScorePercentage)
                .average()
                .orElse(0.0);

        long perfectScoreCount = attempts.stream()
                .filter(attempt -> Double.compare(attempt.getScorePercentage(), 100.0) == 0)
                .count();

        long passedAssessments = attempts.stream()
                .filter(attempt -> Boolean.TRUE.equals(attempt.getPassed()))
                .count();

        int streakDays = calculateCurrentStreak(attempts);

        log.info("""
                        Badge Evaluation Stats
                        ----------------------
                        Assessments      : {}
                        Average Score    : {}
                        Perfect Scores   : {}
                        Passed           : {}
                        Current Streak   : {}
                        """,
                assessmentCount,
                averageScore,
                perfectScoreCount,
                passedAssessments,
                streakDays);

        for (BadgeResponseDTO badge : allBadges) {

            if (userBadgeRepository.existsByUserIdAndBadgeId(userId, badge.getId())) {
                continue;
            }

            boolean eligible = switch (badge.getCriteriaType()) {

                case "ASSESSMENT_COUNT" -> assessmentCount >= badge.getCriteriaValue();

                case "AVERAGE_SCORE" -> averageScore >= badge.getCriteriaValue();

                case "PERFECT_SCORE_COUNT" -> perfectScoreCount >= badge.getCriteriaValue();

                case "PASSED_ASSESSMENTS" -> passedAssessments >= badge.getCriteriaValue();

                case "STREAK_DAYS" -> streakDays >= badge.getCriteriaValue();

                default -> false;
            };

            if (eligible) {

                log.info("Awarding badge '{}' to user {}",
                        badge.getName(),
                        userId);

                userService.awardBadge(
                        AwardBadgeRequestDTO.builder()
                                .userId(userId)
                                .badgeId(badge.getId())
                                .build()
                );
            }
        }
    }

    private int calculateCurrentStreak(List<AttemptResultDTO> attempts) {

        Set<LocalDate> completedDates = attempts.stream()
                .map(a -> a.getSubmittedAt().toLocalDate())
                .collect(Collectors.toSet());

        LocalDate today = LocalDate.now();

        int streak = 0;

        while (completedDates.contains(today.minusDays(streak))) {
            streak++;
        }

        return streak;
    }
}
