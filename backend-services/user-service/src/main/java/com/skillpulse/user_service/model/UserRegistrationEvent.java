package com.skillpulse.user_service.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationEvent {

    private Long userId;

    private String fullName;

    private String email;

    private String role;

    private LocalDateTime registeredAt;
}
