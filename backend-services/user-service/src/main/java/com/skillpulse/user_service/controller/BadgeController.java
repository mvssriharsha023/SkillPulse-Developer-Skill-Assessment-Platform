package com.skillpulse.user_service.controller;

import com.skillpulse.user_service.model.AwardBadgeRequestDTO;
import com.skillpulse.user_service.model.BadgeResponseDTO;
import com.skillpulse.user_service.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/badges")
public class BadgeController {

    private final UserService userService;

    public BadgeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<BadgeResponseDTO>> getAllBadges() {
        List<BadgeResponseDTO> badges = userService.getAllBadges();

        return ResponseEntity.status(HttpStatus.OK).body(badges);
    }

    @PostMapping("/award")
    public ResponseEntity<String> awardBadge(@RequestBody AwardBadgeRequestDTO awardBadgeRequestDTO) {
        userService.awardBadge(awardBadgeRequestDTO);

        return ResponseEntity.status(HttpStatus.OK).body("Successfully awarded badge: " + awardBadgeRequestDTO.getBadgeId() + " to User: " + awardBadgeRequestDTO.getUserId());
    }
}
