package com.skillpulse.user_service.service;

import com.skillpulse.user_service.model.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    // Registration
    UserResponseDTO registerUser(RegisterUserRequestDTO request);

    // Login
    LoginResponseDTO loginUser(LoginRequestDTO request);
    // Retrieval
    UserResponseDTO getUserById(Long id);
    UserResponseDTO getUserByEmail(String email);
    Page<UserResponseDTO> getAllUsers(String role, String status, int page, int size);

    // Profile
    DeveloperProfileResponseDTO getDeveloperProfile(Long userId);
    DeveloperProfileResponseDTO updateDeveloperProfile(Long userId, UpdateProfileRequestDTO request);

    // Badges
    List<BadgeResponseDTO> getAllBadges();
    List<BadgeResponseDTO> getUserBadges(Long userId);
    void awardBadge(AwardBadgeRequestDTO request);

    // Admin operations
    UserResponseDTO updateUserStatus(Long userId, String newStatus);

    // Called by Score Service via Kafka consumer (internal method)
    void updateProfileStats(Long userId, double newScore, boolean passed);
}