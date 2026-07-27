package com.skillpulse.user_service.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private Long id;

    private String fullName;

    private String email;

    private String role;

    private String bio;

    private String githubUrl;

    private String status;

    private LocalDateTime createdAt;
}
