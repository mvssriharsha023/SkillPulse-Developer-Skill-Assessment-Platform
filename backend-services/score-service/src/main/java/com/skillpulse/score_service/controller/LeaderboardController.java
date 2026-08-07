package com.skillpulse.score_service.controller;

import com.skillpulse.score_service.entity.LeaderboardEntryEntity;
import com.skillpulse.score_service.mapper.ScoreMapper;
import com.skillpulse.score_service.model.LeaderboardEntryDTO;
import com.skillpulse.score_service.model.SkillScoreDTO;
import com.skillpulse.score_service.repository.LeaderboardEntryRepository;
import com.skillpulse.score_service.repository.SkillScoreRepository;
import com.skillpulse.score_service.service.LeaderboardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/leaderboard")
@CrossOrigin(origins = "*")
public class LeaderboardController {

    private final LeaderboardEntryRepository leaderboardEntryRepository;
    private final LeaderboardService leaderboardService;
    private final ScoreMapper scoreMapper;

    public LeaderboardController(
            LeaderboardEntryRepository leaderboardEntryRepository,
            LeaderboardService leaderboardService,
            ScoreMapper scoreMapper) {
        this.leaderboardEntryRepository = leaderboardEntryRepository;
        this.leaderboardService = leaderboardService;
        this.scoreMapper = scoreMapper;
    }

    // GET /api/v1/leaderboard
    // Returns top 10 leaderboard entries
    @GetMapping
    public ResponseEntity<List<LeaderboardEntryDTO>> getTop10Leaderboard() {
        List<LeaderboardEntryDTO> top10 = leaderboardEntryRepository
                .findTop10ByOrderByRankPositionAsc()
                .stream()
                .map(scoreMapper::toLeaderboardEntryDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(top10);
    }

    // GET /api/v1/leaderboard/full
    // Returns paginated full leaderboard
    @GetMapping("/full")
    public ResponseEntity<Page<LeaderboardEntryDTO>> getFullLeaderboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PageRequest pageable = PageRequest.of(page, size);
        Page<LeaderboardEntryEntity> entityPage =
                leaderboardEntryRepository.findAllByOrderByRankPositionAsc(pageable);

        Page<LeaderboardEntryDTO> dtoPage = entityPage.map(scoreMapper::toLeaderboardEntryDTO);
        return ResponseEntity.ok(dtoPage);
    }

    // GET /api/v1/leaderboard/skills/{category}
    // Returns top scorers for a specific skill category
    @GetMapping("/skills/{category}")
    public ResponseEntity<List<SkillScoreDTO>> getSkillLeaderboard(
            @PathVariable String category) {

        List<SkillScoreDTO> skillLeaderboard = leaderboardService.getSkillLeaderboard(category);

        return ResponseEntity.ok(skillLeaderboard);
    }

    // GET /api/v1/leaderboard/user/{userId}
    // Returns a specific user's leaderboard position
    @GetMapping("/user/{userId}")
    public ResponseEntity<LeaderboardEntryDTO> getUserLeaderboardPosition(
            @PathVariable Long userId) {
        LeaderboardEntryEntity entry = leaderboardEntryRepository.findByUserId(userId)
                .orElse(null);

        if (entry == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(scoreMapper.toLeaderboardEntryDTO(entry));
    }
}