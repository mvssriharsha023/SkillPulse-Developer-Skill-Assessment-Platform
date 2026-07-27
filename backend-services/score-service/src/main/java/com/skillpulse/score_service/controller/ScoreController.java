package com.skillpulse.score_service.controller;

import com.skillpulse.score_service.model.ScoreResponseDTO;
import com.skillpulse.score_service.model.SkillScoreDTO;
import com.skillpulse.score_service.model.UserScoreSummaryDTO;
import com.skillpulse.score_service.service.ScoreService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scores")
@CrossOrigin(origins = "*")
public class ScoreController {

    private final ScoreService scoreService;

    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    // GET /api/v1/scores/attempt/{attemptId}
    @GetMapping("/attempt/{attemptId}")
    public ResponseEntity<ScoreResponseDTO> getScoreByAttemptId(
            @PathVariable Long attemptId) {
        return ResponseEntity.ok(scoreService.getScoreByAttemptId(attemptId));
    }

    // GET /api/v1/scores/user/{userId}
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ScoreResponseDTO>> getScoresByUserId(
            @PathVariable Long userId) {
        return ResponseEntity.ok(scoreService.getScoresByUserId(userId));
    }

    // GET /api/v1/scores/user/{userId}/summary
    @GetMapping("/user/{userId}/summary")
    public ResponseEntity<UserScoreSummaryDTO> getUserScoreSummary(
            @PathVariable Long userId) {
        return ResponseEntity.ok(scoreService.getUserScoreSummary(userId));
    }

    // GET /api/v1/scores/user/{userId}/skills
    @GetMapping("/user/{userId}/skills")
    public ResponseEntity<List<SkillScoreDTO>> getUserSkillScores(
            @PathVariable Long userId) {
        return ResponseEntity.ok(scoreService.getUserSkillScores(userId));
    }

    // GET /api/v1/scores/category/{skillCategory}
    @GetMapping("/category/{skillCategory}")
    public ResponseEntity<Page<ScoreResponseDTO>> getScoresBySkillCategory(
            @PathVariable String skillCategory,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                scoreService.getScoresBySkillCategory(skillCategory, page, size));
    }
}