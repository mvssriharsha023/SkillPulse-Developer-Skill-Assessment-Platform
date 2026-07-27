package com.skillpulse.user_service.controller;

import com.skillpulse.user_service.model.*;
import com.skillpulse.user_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody RegisterUserRequestDTO userRequestDTO) {
        UserResponseDTO userResponseDTO = userService.registerUser(userRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userResponseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(@RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO loginResponseDTO = userService.loginUser(loginRequestDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(loginResponseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long id) {

        UserResponseDTO userResponseDTO = userService.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(userResponseDTO);
    }

    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(
            @RequestParam(name = "role", required = false) String role,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
    ) {
        Page<UserResponseDTO> userResponseDTOS = userService.getAllUsers(role, status, page, size);

        return ResponseEntity.status(HttpStatus.OK)
                .body(userResponseDTOS);
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<DeveloperProfileResponseDTO> getDeveloperProfile(@PathVariable Long id) {
        DeveloperProfileResponseDTO developerProfileResponseDTO = userService.getDeveloperProfile(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(developerProfileResponseDTO);
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<DeveloperProfileResponseDTO> updateDeveloperProfile(@PathVariable Long id, @RequestBody UpdateProfileRequestDTO requestDTO) {
        DeveloperProfileResponseDTO developerProfileResponseDTO = userService.updateDeveloperProfile(id, requestDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(developerProfileResponseDTO);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponseDTO> updateUserStatus(@PathVariable Long id, @RequestParam(name = "newStatus") String newStatus) {
        UserResponseDTO userResponseDTO = userService.updateUserStatus(id, newStatus);

        return ResponseEntity.status(HttpStatus.OK)
                .body(userResponseDTO);
    }

    @GetMapping("/{id}/badges")
    public ResponseEntity<List<BadgeResponseDTO>> getUserBadges(@PathVariable Long id) {
        List<BadgeResponseDTO> badgeResponseDTO = userService.getUserBadges(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(badgeResponseDTO);
    }

    @PutMapping("/{userId}/profile/stats")
    public ResponseEntity<String> updateProfileStats(@PathVariable Long userId, @RequestBody UpdateProfileStatsRequestDTO updateProfileStatsRequestDTO) {

        userService.updateProfileStats(userId, updateProfileStatsRequestDTO.getNewScore(), updateProfileStatsRequestDTO.getPassed());
        return ResponseEntity.status(HttpStatus.OK)
                .body("Successfully updated profile stats!");
    }
}
