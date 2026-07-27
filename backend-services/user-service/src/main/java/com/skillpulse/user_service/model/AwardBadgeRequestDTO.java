package com.skillpulse.user_service.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AwardBadgeRequestDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Badge ID is required")
    private Long badgeId;
}
