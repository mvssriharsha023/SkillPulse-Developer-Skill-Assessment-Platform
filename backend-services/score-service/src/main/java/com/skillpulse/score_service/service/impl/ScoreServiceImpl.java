package com.skillpulse.score_service.service.impl;

import com.skillpulse.score_service.client.UserServiceClient;
import com.skillpulse.score_service.entity.ScoreEntity;
import com.skillpulse.score_service.exceptions.ResourceNotFoundException;
import com.skillpulse.score_service.mapper.ScoreMapper;
import com.skillpulse.score_service.model.ScoreResponseDTO;
import com.skillpulse.score_service.model.SkillScoreDTO;
import com.skillpulse.score_service.model.UserScoreSummaryDTO;
import com.skillpulse.score_service.model.AssessmentSubmittedEvent;
import com.skillpulse.score_service.repository.LeaderboardEntryRepository;
import com.skillpulse.score_service.repository.ScoreRepository;
import com.skillpulse.score_service.repository.SkillScoreRepository;
import com.skillpulse.score_service.service.ScoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ScoreServiceImpl implements ScoreService {

    private final ScoreRepository scoreRepository;
    private final LeaderboardEntryRepository leaderboardEntryRepository;
    private final SkillScoreRepository skillScoreRepository;
    private final ScoreMapper scoreMapper;
    private final UserServiceClient userServiceClient;
    private final DataSource dataSource;

    public ScoreServiceImpl(
            ScoreRepository scoreRepository,
            LeaderboardEntryRepository leaderboardEntryRepository,
            SkillScoreRepository skillScoreRepository,
            ScoreMapper scoreMapper,
            UserServiceClient userServiceClient,
            DataSource dataSource) {
        this.scoreRepository = scoreRepository;
        this.leaderboardEntryRepository = leaderboardEntryRepository;
        this.skillScoreRepository = skillScoreRepository;
        this.scoreMapper = scoreMapper;
        this.userServiceClient = userServiceClient;
        this.dataSource = dataSource;
    }

    @Override
    public boolean isAlreadyScored(Long attemptId) {
        return scoreRepository.existsByAttemptId(attemptId);
    }

    @Override
    @Transactional
    public void processScore(AssessmentSubmittedEvent event) {
        log.info("Processing score for attemptId={}, userId={}, score={}%",
                event.getAttemptId(), event.getUserId(), event.getScorePercentage());

        // Step 1: Save the score record
        ScoreEntity scoreEntity = ScoreEntity.builder()
                .userId(event.getUserId())
                .assessmentId(event.getAssessmentId())
                .attemptId(event.getAttemptId())
                .rawScore(event.getRawScore())
                .maxScore(event.getMaxScore())
                .scorePercentage(event.getScorePercentage())
                .correctAnswers(event.getCorrectAnswers())
                .totalQuestions(event.getTotalQuestions())
                .timeTakenSeconds(event.getTimeTakenSeconds())
                .passed(event.getPassed())
                .skillCategory(event.getSkillCategory())
                .difficulty(event.getDifficulty())
                .build();

        ScoreEntity savedScore = scoreRepository.save(scoreEntity);
        log.info("Score saved with id={}", savedScore.getId());

        // Step 2: Update skill scores via stored procedure
        callUpdateSkillScore(
                event.getUserId(),
                event.getSkillCategory(),
                event.getScorePercentage(),
                event.getPassed()
        );
        log.info("Skill score updated for userId={}, skill={}", event.getUserId(), event.getSkillCategory());

        // Step 3: Update leaderboard via stored procedure
        callUpdateLeaderboard(
                event.getUserId(),
                event.getUserFullName() != null ? event.getUserFullName() : "Unknown",
                savedScore.getId()
        );
        log.info("Leaderboard updated for userId={}", event.getUserId());

        // Step 4: Update developer profile stats in User Service (best-effort REST call)
        userServiceClient.updateProfileStats(
                event.getUserId(),
                event.getScorePercentage(),
                event.getPassed()
        );
        log.info("Profile stats update requested for userId={}", event.getUserId());
    }

    @Override
    public ScoreResponseDTO getScoreByAttemptId(Long attemptId) {
        ScoreEntity score = scoreRepository.findByAttemptId(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Score not found for attemptId: " + attemptId));
        return scoreMapper.toScoreResponseDTO(score);
    }

    @Override
    public List<ScoreResponseDTO> getScoresByUserId(Long userId) {
        return scoreRepository.findByUserIdOrderByScoredAtDesc(userId)
                .stream()
                .map(scoreMapper::toScoreResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserScoreSummaryDTO getUserScoreSummary(Long userId) {
        Long totalScorePoints = scoreRepository.sumRawScoreByUserId(userId);
        long assessmentsTaken = scoreRepository.countByUserId(userId);
        long assessmentsPassed = scoreRepository.countByUserIdAndPassedTrue(userId);
        Double avgPercentage = scoreRepository.avgScorePercentageByUserId(userId);

        List<SkillScoreDTO> skillScores = getUserSkillScores(userId);

        List<ScoreResponseDTO> recentScores = scoreRepository
                .findByUserIdOrderByScoredAtDesc(userId)
                .stream()
                .limit(10)
                .map(scoreMapper::toScoreResponseDTO)
                .collect(Collectors.toList());

        return UserScoreSummaryDTO.builder()
                .userId(userId)
                .totalScorePoints(totalScorePoints != null ? totalScorePoints : 0L)
                .assessmentsTaken((int) assessmentsTaken)
                .assessmentsPassed((int) assessmentsPassed)
                .averagePercentage(avgPercentage != null ? avgPercentage : 0.0)
                .skillScores(skillScores)
                .recentScores(recentScores)
                .build();
    }

    @Override
    public List<SkillScoreDTO> getUserSkillScores(Long userId) {
        return skillScoreRepository.findByUserId(userId)
                .stream()
                .map(scoreMapper::toSkillScoreDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ScoreResponseDTO> getScoresBySkillCategory(String skillCategory, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("scoredAt").descending());
        return scoreRepository.findBySkillCategory(skillCategory, pageable)
                .map(scoreMapper::toScoreResponseDTO);
    }

    // ─────────────────────────────────────────
    // Stored Procedure Calls
    // ─────────────────────────────────────────

    private void callUpdateSkillScore(Long userId, String skillCategory,
                                      Double scorePercentage, Boolean passed) {
        try {
            SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                    .withProcedureName("UpdateSkillScore")
                    .declareParameters(
                            new org.springframework.jdbc.core.SqlParameter("p_user_id", Types.BIGINT),
                            new org.springframework.jdbc.core.SqlParameter("p_skill_category", Types.VARCHAR),
                            new org.springframework.jdbc.core.SqlParameter("p_score_pct", Types.DECIMAL),
                            new org.springframework.jdbc.core.SqlParameter("p_passed", Types.BOOLEAN)
                    );

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("p_user_id", userId)
                    .addValue("p_skill_category", skillCategory)
                    .addValue("p_score_pct", scorePercentage)
                    .addValue("p_passed", passed);

            call.execute(params);

        } catch (Exception ex) {
            log.error("Failed to call UpdateSkillScore procedure for userId={}: {}",
                    userId, ex.getMessage());
            throw ex;
        }
    }

    private void callUpdateLeaderboard(Long userId, String fullName, Long scoreId) {
        try {
            SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                    .withProcedureName("UpdateLeaderboard")
                    .declareParameters(
                            new org.springframework.jdbc.core.SqlParameter("p_user_id", Types.BIGINT),
                            new org.springframework.jdbc.core.SqlParameter("p_full_name", Types.VARCHAR),
                            new org.springframework.jdbc.core.SqlParameter("p_score_id", Types.BIGINT)
                    );

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("p_user_id", userId)
                    .addValue("p_full_name", fullName)
                    .addValue("p_score_id", scoreId);

            call.execute(params);

        } catch (Exception ex) {
            log.error("Failed to call UpdateLeaderboard procedure for userId={}: {}",
                    userId, ex.getMessage());
            throw ex;
        }
    }
}