package com.skillpulse.user_service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AwardBadgeEvent {

    private Long userId;

    private List<AttemptResultDTO> attempts;
}