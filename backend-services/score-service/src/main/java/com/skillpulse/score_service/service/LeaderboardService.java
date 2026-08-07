package com.skillpulse.score_service.service;

import com.skillpulse.score_service.model.SkillScoreDTO;

import java.util.List;

public interface LeaderboardService {

    List<SkillScoreDTO> getSkillLeaderboard(String category);
}
