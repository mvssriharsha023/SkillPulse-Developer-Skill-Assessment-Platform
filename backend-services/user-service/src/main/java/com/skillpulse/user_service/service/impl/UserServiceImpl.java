package com.skillpulse.user_service.service.impl;

import com.skillpulse.user_service.entity.BadgeEntity;
import com.skillpulse.user_service.entity.DeveloperProfileEntity;
import com.skillpulse.user_service.entity.UserBadgeEntity;
import com.skillpulse.user_service.entity.UserEntity;
import com.skillpulse.user_service.exception.DuplicateEmailException;
import com.skillpulse.user_service.exception.InvalidCredentialsException;
import com.skillpulse.user_service.exception.ResourceNotFoundException;
import com.skillpulse.user_service.mapper.UserMapper;
import com.skillpulse.user_service.messaging.KafkaProducer;
import com.skillpulse.user_service.model.*;
import com.skillpulse.user_service.respository.BadgeRepository;
import com.skillpulse.user_service.respository.DeveloperProfileRepository;
import com.skillpulse.user_service.respository.UserBadgeRepository;
import com.skillpulse.user_service.respository.UserRepository;
import com.skillpulse.user_service.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;

    private final BadgeRepository badgeRepository;

    private final UserBadgeRepository userBadgeRepository;

    private final DeveloperProfileRepository developerProfileRepository;

    private final UserMapper userMapper;

    private final KafkaProducer kafkaProducer;


    public UserServiceImpl(UserRepository userRepository, BadgeRepository badgeRepository, UserBadgeRepository userBadgeRepository, DeveloperProfileRepository developerProfileRepository, UserMapper userMapper, KafkaProducer kafkaProducer) {
        this.userRepository = userRepository;
        this.badgeRepository = badgeRepository;
        this.userBadgeRepository = userBadgeRepository;
        this.developerProfileRepository = developerProfileRepository;
        this.userMapper = userMapper;
        this.kafkaProducer = kafkaProducer;
    }
    @Override
    public UserResponseDTO registerUser(RegisterUserRequestDTO request) {
        Optional<UserEntity> user = userRepository.findByEmail(request.getEmail());

        if (user.isPresent()) {
            throw new DuplicateEmailException("Email already exists!");
        }


        UserEntity userEntity = UserEntity.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(request.getPassword())
                .status("ACTIVE")
                .role(request.getRole())
                .bio(request.getBio())
                .githubUrl(request.getGithubUrl())
                .build();

        UserEntity savedUser = userRepository.save(userEntity);

        UserRegistrationEvent userRegistrationEvent = UserRegistrationEvent.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .role(savedUser.getRole())
                .registeredAt(savedUser.getCreatedAt())
                .build();

        kafkaProducer.publish(userRegistrationEvent);

        return userMapper.userToUserResponseDTO(userEntity);
    }

    @Override
    public LoginResponseDTO loginUser(LoginRequestDTO request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        if (!request.getPassword().equals(user.getPasswordHash())) {
            throw new InvalidCredentialsException("Wrong password! Please try again!");
        }

        return new LoginResponseDTO("token");
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User nto found!"));

        return userMapper.userToUserResponseDTO(user);
    }

    @Override
    public UserResponseDTO getUserByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        return userMapper.userToUserResponseDTO(user);
    }

    @Override
    public Page<UserResponseDTO> getAllUsers(String role, String status, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<UserEntity> user;
        if (role == null || role.isEmpty()) {
            user = userRepository.findAll(pageable);
        } else {
            user = userRepository.findByRole(role, pageable);
        }


        return user.map(userMapper::userToUserResponseDTO);
    }

    @Override
    public DeveloperProfileResponseDTO getDeveloperProfile(Long userId) {
        DeveloperProfileEntity developerProfile = developerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Developer Profile not found!"));

        return userMapper.developerProfileEntityToResponseDTO(developerProfile);
    }

    @Override
    public DeveloperProfileResponseDTO updateDeveloperProfile(Long userId, UpdateProfileRequestDTO request) {

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        DeveloperProfileEntity developerProfile = developerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Developer Profile not found!"));

        userEntity.setBio(request.getBio());
        userEntity.setGithubUrl(request.getGithubUrl());

        userRepository.save(userEntity);

        developerProfile.setExperienceYears(request.getExperienceYears());
        developerProfile.setPrimarySkill(request.getPrimarySkill());

        DeveloperProfileEntity developerProfile1 = developerProfileRepository.save(developerProfile);

        return userMapper.developerProfileEntityToResponseDTO(developerProfile1);

    }

    @Override
    public List<BadgeResponseDTO> getAllBadges() {
        List<BadgeEntity> badges = badgeRepository.findAll();

        return badges.stream().map(userMapper::badgeEntityToResponseDTO).toList();
    }

    @Override
    public List<BadgeResponseDTO> getUserBadges(Long userId) {
        List<UserBadgeEntity> userBadgeEntities = userBadgeRepository.findByUserId(userId);

        List<BadgeResponseDTO> badgeResponseDTOList = new ArrayList<>();

        for (UserBadgeEntity userBadge : userBadgeEntities) {
            BadgeEntity badge = badgeRepository.findById(userBadge.getBadgeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Badge not found!"));

            badgeResponseDTOList.add(userMapper.badgeEntityToResponseDTO(badge));
        }

        return badgeResponseDTOList;
    }

    @Override
    public void awardBadge(AwardBadgeRequestDTO request) {
        Optional<UserEntity> userEntity = userRepository.findById(request.getUserId());

        if (userEntity.isEmpty()) {
            throw new ResourceNotFoundException("User not found with id: " +  request.getUserId());
        }

        Optional<BadgeEntity> badgeEntity = badgeRepository.findById(request.getBadgeId());

        if (badgeEntity.isEmpty()) {
            throw new ResourceNotFoundException("Badge not found with id: " +  request.getBadgeId());
        }

        boolean alreadyAwarded = userBadgeRepository.existsByUserIdAndBadgeId(request.getUserId(), request.getBadgeId());

        if (alreadyAwarded) {
            return;
        }

        UserBadgeEntity userBadgeEntity = UserBadgeEntity.builder()
                .userId(request.getUserId())
                .badgeId(request.getBadgeId())
                .build();

        UserBadgeEntity savedUserBadgeEntity = userBadgeRepository.save(userBadgeEntity);

        // Update developer profile
        DeveloperProfileEntity developerProfile = developerProfileRepository.findByUserId(savedUserBadgeEntity.getUserId())
                .orElseThrow( () -> new ResourceNotFoundException("Developer Profile not found with id: " +  savedUserBadgeEntity.getUserId()));

        developerProfile.setTotalBadges(developerProfile.getTotalBadges() + 1);

        developerProfileRepository.save(developerProfile);
    }

    @Override
    public UserResponseDTO updateUserStatus(Long userId, String newStatus) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (!user.getStatus().equals(newStatus)) {
            user.setStatus(newStatus);
            userRepository.save(user);
        }

        return userMapper.userToUserResponseDTO(user);
    }

    @Override
    public void updateProfileStats(Long userId, double newScore, boolean passed) {
        DeveloperProfileEntity developerProfile = developerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Developer Profile not found with id: " +  userId));

        double newAvg = ((developerProfile.getAverageScore() * developerProfile.getTotalAssessments()) + newScore) / (developerProfile.getTotalAssessments()) + 1;
        developerProfile.setAverageScore(newAvg);
        developerProfile.setTotalAssessments(developerProfile.getTotalAssessments() + 1);

        if (passed) {
            if (newAvg >= 90) {
                developerProfile.setRankTier("DIAMOND");
            }
            else if (newAvg >= 80) {
                developerProfile.setRankTier("PLATINUM");
            }
            else if (newAvg >= 70) {
                developerProfile.setRankTier("GOLD");
            }
            else if (newAvg >= 60) {
                developerProfile.setRankTier("SILVER");
            }
            else {
                developerProfile.setRankTier("BRONZE");
            }
        }
        developerProfile.setLastActiveAt(LocalDateTime.now());

        developerProfileRepository.save(developerProfile);
    }
}
