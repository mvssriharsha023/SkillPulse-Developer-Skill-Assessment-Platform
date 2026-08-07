package com.skillpulse.score_service.service.impl;

import com.skillpulse.score_service.client.UserServiceClient;
import com.skillpulse.score_service.entity.SkillScoreEntity;
import com.skillpulse.score_service.mapper.ScoreMapper;
import com.skillpulse.score_service.model.SkillScoreDTO;
import com.skillpulse.score_service.repository.SkillScoreRepository;
import com.skillpulse.score_service.service.LeaderboardService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LeaderboardServiceImpl implements LeaderboardService {

    private final SkillScoreRepository skillScoreRepository;
    private final ScoreMapper scoreMapper;
    private final UserServiceClient userServiceClient;

    public LeaderboardServiceImpl(SkillScoreRepository skillScoreRepository, UserServiceClient userServiceClient, ScoreMapper scoreMapper) {
        this.skillScoreRepository = skillScoreRepository;
        this.userServiceClient = userServiceClient;
        this.scoreMapper = scoreMapper;
    }
    @Override
    public List<SkillScoreDTO> getSkillLeaderboard(String category) {

        List<SkillScoreEntity> skillScoreEntities = skillScoreRepository
                .findBySkillCategoryOrderByAverageScoreDesc(category);

        List<SkillScoreDTO> skillLeaderboard = new ArrayList<>();
        for (SkillScoreEntity skillScoreEntity : skillScoreEntities) {
            UserServiceClient.UserClientDTO userClientDTO = userServiceClient.getUserDetails(skillScoreEntity.getUserId());

            SkillScoreDTO skillScoreDTO = scoreMapper.toSkillScoreDTO(skillScoreEntity);
            skillScoreDTO.setFullName(userClientDTO.getFullName());
            skillLeaderboard.add(skillScoreDTO);
        }

        return skillLeaderboard;
    }
}
